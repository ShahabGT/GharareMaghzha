package ir.ghararemaghzha.game.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.activities.MainActivity
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.classes.Utils
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.NetworkApi
import ir.ghararemaghzha.game.data.RemoteDataSource
import ir.ghararemaghzha.game.data.Resource
import ir.hamsaa.persiandatepicker.Listener
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog
import ir.hamsaa.persiandatepicker.util.PersianCalendar
import ir.shahabazimi.instagrampicker.InstagramPicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class ProfileEditFragment : Fragment(R.layout.fragment_profile_edit) {

    private lateinit var avatar: ImageView
    private lateinit var name: TextInputEditText
    private lateinit var email: TextInputEditText
    private lateinit var bday: TextInputEditText
    private lateinit var invite: TextInputEditText
    private lateinit var inviteLayout: TextInputLayout
    private lateinit var bdayView: View
    private lateinit var avatarChange: MaterialTextView
    private lateinit var avatarRemove: MaterialTextView
    private lateinit var save: MaterialButton
    private lateinit var female: CheckBox
    private lateinit var male: CheckBox
    private lateinit var loading: ConstraintLayout

    private var uploading = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<ImageView>(R.id.toolbar_avatar).isEnabled = false
    }

    override fun onStop() {
        super.onStop()
        requireActivity().findViewById<ImageView>(R.id.toolbar_avatar).isEnabled = true
    }

    private fun init(v: View) {
        requireActivity().findViewById<MaterialTextView>(R.id.toolbar_title).setText(R.string.profile_edit_subtitle)
        uploading = false
        name = v.findViewById(R.id.profile_name)
        invite = v.findViewById(R.id.profile_invite)
        inviteLayout = v.findViewById(R.id.profile_invite_layout)
        val number: TextInputEditText = v.findViewById(R.id.profile_number)
        email = v.findViewById(R.id.profile_email)
        bday = v.findViewById(R.id.profile_bday)
        bdayView = v.findViewById(R.id.profile_bday_view)
        avatar = v.findViewById(R.id.profile_avatar)
        avatarChange = v.findViewById(R.id.profile_avatar_change)
        avatarRemove = v.findViewById(R.id.profile_avatar_remove)
        save = v.findViewById(R.id.profile_save)
        male = v.findViewById(R.id.profile_male)
        female = v.findViewById(R.id.profile_female)
        loading = v.findViewById(R.id.profile_loading)
        name.setText(MySharedPreference.getInstance(requireContext()).getUsername())
        number.setText(MySharedPreference.getInstance(requireContext()).getNumber())
        bday.setText(MySharedPreference.getInstance(requireContext()).getUserBirthday())
        email.setText(MySharedPreference.getInstance(requireContext()).getUserEmail())
        val sex = MySharedPreference.getInstance(requireContext()).getUserSex()
        if (sex == "male") {
            male.isChecked = true
            female.isChecked = false
        } else if (sex == "female") {
            male.isChecked = false
            female.isChecked = true
        }
        if (MySharedPreference.getInstance(requireContext()).getUserInvite().isNotEmpty()) {
            invite.setText(MySharedPreference.getInstance(requireContext()).getUserInvite())
            invite.isEnabled = false
            inviteLayout.isEnabled = false
        }
        Glide.with(this)
                .load(getString(R.string.avatar_url, MySharedPreference.getInstance(requireContext()).getUserAvatar()))
                .circleCrop()
                .placeholder(R.drawable.placeholder)
                .into(avatar)
        if (MySharedPreference.getInstance(requireContext()).getUserAvatar().isEmpty()) avatarRemove.visibility = View.GONE
        onClicks()
    }

    private fun onClicks() {
        avatarChange.setOnClickListener {
            val picker = InstagramPicker(requireActivity())
            picker.show('1'.toInt(), '1'.toInt()) { address: String? ->
                if (Utils.checkInternet(requireContext())) {
                    if (!uploading) {
                        save.isEnabled = false
                        loading.visibility = View.VISIBLE
                        uploading = true
                        CoroutineScope(Dispatchers.IO).launch {
                            changeAvatar(Uri.parse(address))
                        }
                    }
                } else Toast.makeText(context, getString(R.string.internet_error), Toast.LENGTH_SHORT).show()
            }
        }
        avatarRemove.setOnClickListener {
            if (Utils.checkInternet(requireContext())) {
                val avatarName = MySharedPreference.getInstance(requireContext()).getUserAvatar()
                if (avatarName.isNotEmpty()) {
                    save.isEnabled = false
                    loading.visibility = View.VISIBLE
                    CoroutineScope(Dispatchers.IO).launch {
                        removeAvatar(avatarName)
                    }
                }
            } else Toast.makeText(requireContext(), getString(R.string.internet_error), Toast.LENGTH_SHORT).show()
        }
        bdayView.setOnClickListener { selectDate() }
        male.setOnCheckedChangeListener { _, b: Boolean -> if (b) female.isChecked = false }
        female.setOnCheckedChangeListener { _, b: Boolean -> if (b) male.isChecked = false }
        save.setOnClickListener {
            var i = ""
            if (invite.isEnabled && invite.text.toString().trim { it <= ' ' }.isNotEmpty())
                i = invite.text.toString()
            val n = name.text.toString()
            val e = email.text.toString()
            val b = bday.text.toString()
            var s = ""
            if (female.isChecked && !male.isChecked)
                s = "female"
            else if (male.isChecked && !female.isChecked)
                s = "male"
            if (n.isEmpty() || n.length < 6)
                Toast.makeText(context, getString(R.string.general_name_form_error), Toast.LENGTH_SHORT).show()
            else if (e.isNotEmpty() && !Utils.isEmailValid(e))
                Toast.makeText(context, getString(R.string.general_email_form_error), Toast.LENGTH_SHORT).show()
            else if (Utils.checkInternet(requireContext())) {
                save.isEnabled = false
                loading.visibility = View.VISIBLE
                save.text = "..."
                CoroutineScope(Dispatchers.IO).launch {
                    updateProfile(n, e, b, s, i)
                }
            } else Toast.makeText(context, getString(R.string.internet_error), Toast.LENGTH_SHORT).show()
        }
    }

    @Suppress("deprecation")
    private fun selectDate() {
        val color: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            resources.getColor(R.color.colorPrimary, null)
        else
            resources.getColor(R.color.colorPrimary)
        PersianDatePickerDialog(context)
                .setPositiveButtonString("انتخاب")
                .setNegativeButton("بستن")
                .setTodayButton("امروز")
                .setMinYear(1300)
                .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
                .setActionTextColor(color)
                .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
                .setShowInBottomSheet(true)
                .setListener(object : Listener {
                    override fun onDateSelected(persianCalendar: PersianCalendar) {
                        val date = "${persianCalendar.persianYear}/${persianCalendar.persianMonth}/${persianCalendar.persianDay}"
                        bday.setText(date)
                    }

                    override fun onDismissed() {}
                }).show()
    }

    private fun toBase64(path: Uri): String {
        val bitmap: Bitmap = scale(path)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

//    private fun getOrientation2(photoUri: Uri): Int {
//        val cursor = requireActivity().contentResolver.query(photoUri, arrayOf(MediaStore.MediaColumns.ORIENTATION), null, null, null)
//        return if (cursor != null) {
//            if (cursor.count != 1) {
//                cursor.close()
//                return -1
//            }
//            cursor.moveToFirst()
//            cursor.getInt(0)
//        } else -1
//    }

    private fun getOrientation(filepath: Uri): Int =
            when (ExifInterface(filepath.path
                    ?: "").getAttributeInt(ExifInterface.TAG_ORIENTATION, -1)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }


    private fun scale(photoUri: Uri): Bitmap {
        var inputStream = requireActivity().contentResolver.openInputStream(photoUri)
        val dbo = BitmapFactory.Options()
        dbo.inJustDecodeBounds = true
        BitmapFactory.decodeStream(inputStream, null, dbo)
        inputStream?.close()
        val rotatedWidth: Int
        val rotatedHeight: Int
        val orientation = getOrientation(photoUri)
        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight
            rotatedHeight = dbo.outWidth
        } else {
            rotatedWidth = dbo.outWidth
            rotatedHeight = dbo.outHeight
        }
        val maxImageDimension = 500
        var srcBitmap: Bitmap
        inputStream = requireActivity().contentResolver.openInputStream(photoUri)
        if (rotatedWidth > maxImageDimension || rotatedHeight > maxImageDimension) {
            val widthRatio = rotatedWidth.toFloat() / maxImageDimension.toFloat()
            val heightRatio = rotatedHeight.toFloat() / maxImageDimension.toFloat()
            val maxRatio = widthRatio.coerceAtLeast(heightRatio)
            val options = BitmapFactory.Options()
            options.inSampleSize = maxRatio.toInt()
            srcBitmap = BitmapFactory.decodeStream(inputStream, null, options)!!
        } else {
            srcBitmap = BitmapFactory.decodeStream(inputStream)
        }
        inputStream?.close()
        if (orientation > 0) {
            val matrix = Matrix()
            matrix.postRotate(orientation.toFloat())
            if (srcBitmap != null) srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.width,
                    srcBitmap.height, matrix, true)
        }
        return srcBitmap
    }

    private suspend fun changeAvatar(image: Uri) {
        val number = MySharedPreference.getInstance(requireContext()).getNumber()
        val token = MySharedPreference.getInstance(requireContext()).getAccessToken()
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(requireActivity(), true)
            return
        }

        val pic = toBase64(image)
        val avatarName = MySharedPreference.getInstance(requireContext()).getUserId() + Utils.currentDate().replace("-", "").replace(":", "").replace(" ", "")

        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).alterAvatar("Bearer $token", number, "change", pic, avatarName)) {
            is Resource.Success -> {

                withContext(Dispatchers.Main) {
                    save.isEnabled = true
                    uploading = false
                    loading.visibility = View.GONE
                    if (res.value.result == "success") {
                        Toast.makeText(requireContext(), getString(R.string.general_save), Toast.LENGTH_SHORT).show()
                        MySharedPreference.getInstance(requireContext()).setUserAvatar(avatarName)
                        Glide.with(requireContext())
                                .load(getString(R.string.avatar_url, avatarName))
                                .circleCrop()
                                .placeholder(R.drawable.placeholder)
                                .into(avatar)
                        avatarRemove.visibility = View.VISIBLE
                        MainActivity.setAvatars(activity)
                    } else {
                        Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            is Resource.Failure -> {
                withContext(Dispatchers.Main) {
                    if (res.errorCode == 401)
                        Utils.logout(requireActivity(), true)
                    else {
                        save.isEnabled = true
                        uploading = false
                        loading.visibility = View.GONE
                        Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private suspend fun removeAvatar(avatarName: String) {
        val number = MySharedPreference.getInstance(requireContext()).getNumber()
        val token = MySharedPreference.getInstance(requireContext()).getAccessToken()
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(requireActivity(), true)
            return
        }

        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).alterAvatar("Bearer $token", number, "remove", "", avatarName)) {
            is Resource.Success -> {
                withContext(Dispatchers.Main) {
                    save.isEnabled = true
                    uploading = false
                    loading.visibility = View.GONE
                    if (res.value.result == "success") {
                        Toast.makeText(requireContext(), getString(R.string.general_save), Toast.LENGTH_SHORT).show()
                        MySharedPreference.getInstance(requireContext()).setUserAvatar("")
                        Glide.with(requireContext())
                                .load(getString(R.string.avatar_url, MySharedPreference.getInstance(requireContext()).getUserAvatar()))
                                .circleCrop()
                                .placeholder(R.drawable.placeholder)
                                .into(avatar)
                        avatarRemove.visibility = View.VISIBLE
                        MainActivity.setAvatars(activity)
                    } else {
                        Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            is Resource.Failure -> {
                withContext(Dispatchers.Main) {
                    if (res.errorCode == 401)
                        Utils.logout(requireActivity(), true)
                    else {
                        save.isEnabled = true
                        uploading = false
                        loading.visibility = View.GONE
                        Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private suspend fun updateProfile(name: String, email: String, bday: String, sex: String, inviteCode: String) {
        val number = MySharedPreference.getInstance(requireContext()).getNumber()
        val token = MySharedPreference.getInstance(requireContext()).getAccessToken()
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(requireActivity(), true)
            return
        }

        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java))
                .updateProfile("Bearer $token", number, name, email, bday, sex, inviteCode)) {
            is Resource.Success -> {
                withContext(Dispatchers.Main) {
                    save.isEnabled = true
                    save.text = getString(R.string.profile_save)
                    loading.visibility = View.GONE
                    if (res.value.result == "success") {
                        Toast.makeText(context, getString(R.string.general_save), Toast.LENGTH_SHORT).show()
                        MySharedPreference.getInstance(requireContext()).setUsername(name)
                        MySharedPreference.getInstance(requireContext()).setUserEmail(email)
                        MySharedPreference.getInstance(requireContext()).setUserSex(sex)
                        MySharedPreference.getInstance(requireContext()).setUserBirthday(bday)

                        when (res.value.message) {
                            "invite not found" -> Toast.makeText(context, getString(R.string.profile_invite_notfound), Toast.LENGTH_SHORT).show()
                            "invite failed" -> Toast.makeText(context, getString(R.string.profile_invite_failed), Toast.LENGTH_SHORT).show()
                            "invite ok" -> {
                                MySharedPreference.getInstance(requireContext()).setUserInvite(inviteCode)
                                invite.isEnabled = false
                                inviteLayout.isEnabled = false
                            }
                        }
                    } else {
                        Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            is Resource.Failure -> {
                withContext(Dispatchers.Main) {
                    if (res.errorCode == 401)
                        Utils.logout(requireActivity(), true)
                    else {
                        save.isEnabled = true
                        save.text = getString(R.string.profile_save)
                        loading.visibility = View.GONE
                        Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

}