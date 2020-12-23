package ir.ghararemaghzha.game.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import io.realm.Realm
import io.realm.kotlin.where
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.adapters.ContactsAdapter
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.classes.Utils
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.NetworkApi
import ir.ghararemaghzha.game.data.RemoteDataSource
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.models.ContactBody
import ir.ghararemaghzha.game.models.ContactBodyModel
import ir.ghararemaghzha.game.models.ContactsModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ContactsFragment : Fragment(R.layout.fragment_contacts) {

    private val readContactsRequestCode = 5162
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ContactsAdapter
    private lateinit var db: Realm
    private lateinit var loading: ConstraintLayout
    private lateinit var empty: LinearLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)

    }

    private fun init(v: View) {
        requireActivity().findViewById<MaterialTextView>(R.id.toolbar_title).setText(R.string.contacts_title)
        db = Realm.getDefaultInstance()

        loading = v.findViewById(R.id.contacts_loading)
        empty = v.findViewById(R.id.contacts_empty)
        v.findViewById<MaterialButton>(R.id.contacts_empty_retry).setOnClickListener {
            requestContacts()
        }

        recyclerView = v.findViewById(R.id.contacts_recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val data = db.where<ContactsModel>().findAll()
        adapter = ContactsAdapter(requireActivity(), data)
        recyclerView.adapter = adapter
        if (data.size == 0)
            requestContacts()
        else {
            loading.visibility = View.GONE
            empty.visibility = View.GONE
        }
    }

    private fun requestContacts() =
            if (checkPermission() != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                                Manifest.permission.READ_CONTACTS)) {
                    showExplanation()
                } else if (!MySharedPreference.getInstance(requireContext()).getContactsPermission()) {
                    requestPermission()
                    MySharedPreference.getInstance(requireContext()).setContactsPermission()
                } else {
                    Toast.makeText(requireContext(), getString(R.string.permission_contacts_toast), Toast.LENGTH_SHORT).show()
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    intent.data = Uri.fromParts("package", requireActivity().packageName, null)
                    startActivity(intent)
                }
            } else
                showContacts()

    private fun checkPermission() = checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS)

    private fun requestPermission() = requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), readContactsRequestCode)

    private fun showExplanation() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.permission_contacts))
        builder.setMessage(getString(R.string.permission_contacts_message))
        builder.setPositiveButton(getString(R.string.permission_allow)
        ) { _: DialogInterface?, _: Int -> requestPermission() }
        builder.setNegativeButton(getString(R.string.permission_dismiss)
        ) { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == readContactsRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showContacts()
            } else {
                loading.visibility = View.GONE
                empty.visibility = View.VISIBLE
            }
        }
    }

    private fun removeDuplicates(list: MutableList<Contacts>): ArrayList<Contacts> {
        val newList: ArrayList<Contacts> = ArrayList()
        for (element in list) {
            if (element.number.startsWith("+98"))
                element.number = element.number.replace("+98", "0")
            else if (element.number.startsWith("0098"))
                element.number = element.number.replaceFirst("0098", "0")
            if (!newList.any { it.number == element.number })
                newList.add(element)

        }
        return newList
    }

    private fun getContacts(): ArrayList<Contacts> {
        val data = mutableListOf<Contacts>()
        val cr: ContentResolver = requireActivity().contentResolver
        val cur: Cursor? = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, null)
        if (cur != null && cur.count > 0) {
            while (cur.moveToNext()) {
                val id: String = cur.getString(cur
                        .getColumnIndex(ContactsContract.Contacts._ID))
                if (cur.getString(cur
                                .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)).toInt() > 0) {
                    val pCur: Cursor? = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                            + " = ?", arrayOf(id), null)
                    if (pCur != null) {
                        while (pCur.moveToNext()) {
                            val name: String = pCur
                                    .getString(pCur
                                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)).replace(" ", "")
                            if (name.isNotEmpty()) {
                                val phoneNo: String = pCur
                                        .getString(pCur
                                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", "").replace("-", "")
                                if (phoneNo.isNotEmpty() && phoneNo.length > 10) {
                                    data.add(Contacts(name, phoneNo))
                                }
                            }
                        }
                        pCur.close()
                    }
                }
            }
            cur.close()
        }
        return removeDuplicates(data)
    }

    private fun showContacts() {
        loading.visibility = View.VISIBLE
        empty.visibility = View.GONE
        val c = getContacts()

        CoroutineScope(Dispatchers.IO).launch {
            getData(c)
        }
    }


    private suspend fun getData(data: ArrayList<Contacts>) {
        val number = MySharedPreference.getInstance(requireContext()).getNumber()
        val token = MySharedPreference.getInstance(requireContext()).getAccessToken()
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(requireActivity(), true)
            return
        }
        val contacts = mutableListOf<ContactBodyModel>()
        data.forEach {
            contacts.add(ContactBodyModel(it.number, it.name))
        }
        val body = ContactBody(number, contacts)

        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).syncContacts("Bearer $token", body)) {
            is Resource.Success -> {
                if (res.value.result == "success" && res.value.data.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        val results = db.where<ContactsModel>().findAll()
                        db.executeTransaction { results.deleteAllFromRealm() }
                        adapter.notifyDataSetChanged()
                        val networkData = res.value.data.sortedByDescending { it.id }

                        networkData.forEach { it.type = 1 }
                        val co = mutableListOf<ContactsModel>()
                        var index = 0
                        co.add(0, ContactsModel(id = getString(R.string.contacts_title1), type = 0))
                        for (model in networkData) {
                            co.add(model)
                        }
                        val firstIndex = networkData.indexOfFirst { it.id == "0" }
                        co.add(firstIndex + 1, ContactsModel(id = getString(R.string.contacts_title2), type = 0))

                        for (model in co) model.contactId = index++

                        db.executeTransaction { it.insertOrUpdate(co) }
                        loading.visibility = View.GONE
                        empty.visibility = View.GONE
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        loading.visibility = View.GONE
                        empty.visibility = View.VISIBLE
                    }
                }
            }
            is Resource.Failure -> {
                if (res.isNetworkError) {
                    withContext(Dispatchers.Main) {
                        if (db.where<ContactsModel>().findAll().size == 0) {
                            loading.visibility = View.GONE
                            empty.visibility = View.VISIBLE
                        } else {
                            loading.visibility = View.GONE
                            empty.visibility = View.GONE
                        }
                        Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                    }
                } else if (res.errorCode == 401) {
                    withContext(Dispatchers.Main) {
                        loading.visibility = View.GONE
                        empty.visibility = View.GONE
                        Utils.logout(requireActivity(), true)
                    }
                }
            }
        }
    }

    data class Contacts(val name: String, var number: String)
}