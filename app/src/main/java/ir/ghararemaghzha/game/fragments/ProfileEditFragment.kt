package ir.ghararemaghzha.game.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.classes.MySharedPreference
import ir.ghararemaghzha.game.classes.Utils
import ir.ghararemaghzha.game.data.ApiRepository
import ir.ghararemaghzha.game.data.NetworkApi
import ir.ghararemaghzha.game.data.RemoteDataSource
import ir.ghararemaghzha.game.data.Resource
import ir.ghararemaghzha.game.databinding.FragmentProfileEditBinding
import ir.ghararemaghzha.game.viewmodels.ProfileEditViewModel
import ir.hamsaa.persiandatepicker.Listener
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog
import ir.hamsaa.persiandatepicker.util.PersianCalendar
import ir.shahabazimi.instagrampicker.InstagramPicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class ProfileEditFragment : BaseFragment<ProfileEditViewModel,FragmentProfileEditBinding>() {

    private var uploading = false
    private var number: String = ""
    private var token: String = ""

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }
    private fun getUserDetails() {
        number = MySharedPreference.getInstance(requireContext()).getNumber()
        token = MySharedPreference.getInstance(requireContext()).getAccessToken()
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(requireActivity(), true)
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<ImageView>(R.id.toolbar_avatar).isEnabled = false
    }

    override fun onStop() {
        super.onStop()
        requireActivity().findViewById<ImageView>(R.id.toolbar_avatar).isEnabled = true
    }

    private fun init() {
        requireActivity().findViewById<MaterialTextView>(R.id.toolbar_title).setText(R.string.profile_edit_subtitle)
        getUserDetails()
        uploading = false
        b.profileName.setText(MySharedPreference.getInstance(requireContext()).getUsername())
        b.profileNumber.setText(MySharedPreference.getInstance(requireContext()).getNumber())
        b.profileBday.setText(MySharedPreference.getInstance(requireContext()).getUserBirthday())
        b.profileEmail.setText(MySharedPreference.getInstance(requireContext()).getUserEmail())
        val sex = MySharedPreference.getInstance(requireContext()).getUserSex()
        if (sex == "male") {
            b.profileMale.isChecked = true
            b.profileFemale.isChecked = false
        } else if (sex == "female") {
            b.profileMale.isChecked = false
            b.profileFemale.isChecked = true
        }
        if (MySharedPreference.getInstance(requireContext()).getUserInvite().isNotEmpty()) {
            b.profileInvite.setText(MySharedPreference.getInstance(requireContext()).getUserInvite())
            b.profileInvite.isEnabled = false
            b.profileInviteLayout.isEnabled = false
        }
        Glide.with(this)
                .load(getString(R.string.avatar_url, MySharedPreference.getInstance(requireContext()).getUserAvatar()))
                .circleCrop()
                .placeholder(R.drawable.placeholder)
                .into(b.profileAvatar)
        if (MySharedPreference.getInstance(requireContext()).getUserAvatar().isEmpty()) b.profileAvatarRemove.visibility = View.GONE
        onClicks()
    }

    private fun onClicks() {
        b.profileAvatarChange.setOnClickListener {
            val picker = InstagramPicker(requireActivity())
            picker.show(1, 1) { address: String ->
                if (Utils.checkInternet(requireContext())) {
                    if (!uploading) {
                        b.profileSave.isEnabled = false
                        b.profileLoading.visibility = View.VISIBLE
                        uploading = true
                        CoroutineScope(Dispatchers.IO).launch {
                            changeAvatar(Uri.parse(address))
                        }
                    }
                } else Toast.makeText(context, getString(R.string.internet_error), Toast.LENGTH_SHORT).show()
            }
        }
        b.profileAvatarRemove.setOnClickListener {
            if (Utils.checkInternet(requireContext())) {
                val avatarName = MySharedPreference.getInstance(requireContext()).getUserAvatar()
                if (avatarName.isNotEmpty()) {
                    b.profileSave.isEnabled = false
                    b.profileLoading.visibility = View.VISIBLE
                    CoroutineScope(Dispatchers.IO).launch {
                        removeAvatar(avatarName)
                    }
                }
            } else Toast.makeText(context, getString(R.string.internet_error), Toast.LENGTH_SHORT).show()
        }
        b.profileBdayView.setOnClickListener { selectDate() }
        b.profileMale.setOnCheckedChangeListener { _, bool: Boolean -> if (bool) b.profileFemale.isChecked = false }
        b.profileFemale.setOnCheckedChangeListener { _, bool: Boolean -> if (bool) b.profileMale.isChecked = false }
        b.profileSave.setOnClickListener {
            var i = ""
            if (b.profileInvite.isEnabled && b.profileInvite.text.toString().trim { it <= ' ' }.isNotEmpty())
                i = b.profileInvite.text.toString()
            val n = b.profileName.text.toString()
            val e = b.profileEmail.text.toString()
            val bd = b.profileBday.text.toString()
            var s = ""
            if (b.profileFemale.isChecked && !b.profileMale.isChecked)
                s = "female"
            else if (b.profileMale.isChecked && !b.profileFemale.isChecked)
                s = "male"
            if (n.isEmpty() || n.length < 6)
                Toast.makeText(context, getString(R.string.general_name_form_error), Toast.LENGTH_SHORT).show()
            else if (e.isNotEmpty() && !Utils.isEmailValid(e))
                Toast.makeText(context, getString(R.string.general_email_form_error), Toast.LENGTH_SHORT).show()
            else if (Utils.checkInternet(requireContext())) {
                b.profileSave.isEnabled = false
                b.profileLoading.visibility = View.VISIBLE
                b.profileSave.text = "..."
                CoroutineScope(Dispatchers.IO).launch {
                    updateProfile(n, e, bd, s, i)
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
        PersianDatePickerDialog(requireContext())
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
                        b.profileBday.setText(date)
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
        val pic = toBase64(image)
        val avatarName = MySharedPreference.getInstance(requireContext()).getUserId() + Utils.currentDate().replace("-", "").replace(":", "").replace(" ", "")

        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).alterAvatar("Bearer $token", number, "change", pic, avatarName)) {
            is Resource.Success -> {
                withContext(Dispatchers.Main) {
                    b.profileSave.isEnabled = true
                    uploading = false
                    b.profileLoading.visibility = View.GONE
                    if (res.value.result == "success") {
                        Toast.makeText(context, getString(R.string.general_save), Toast.LENGTH_SHORT).show()
                        MySharedPreference.getInstance(requireContext()).setUserAvatar(avatarName)
                        Glide.with(requireContext())
                                .load(getString(R.string.avatar_url, avatarName))
                                .circleCrop()
                                .placeholder(R.drawable.placeholder)
                                .into(b.profileAvatar)
                        b.profileAvatarRemove.visibility = View.VISIBLE
                        setAvatars()
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
                        b.profileSave.isEnabled = true
                        uploading = false
                        b.profileLoading.visibility = View.GONE
                        Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setAvatars() {
        Glide.with(requireContext())
                .load(getString(R.string.avatar_url, MySharedPreference.getInstance(requireContext()).getUserAvatar()))
                .circleCrop()
                .placeholder(R.drawable.placeholder)
                .into(requireActivity().findViewById(R.id.navigation_avatar))
        Glide.with(requireContext())
                .load(getString(R.string.avatar_url, MySharedPreference.getInstance(requireContext()).getUserAvatar()))
                .circleCrop()
                .placeholder(R.drawable.placeholder)
                .into(requireActivity().findViewById(R.id.toolbar_avatar))
    }

    private suspend fun removeAvatar(avatarName: String) {
        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java)).alterAvatar("Bearer $token", number, "remove", "", avatarName)) {
            is Resource.Success -> {
                withContext(Dispatchers.Main) {
                    b.profileSave.isEnabled = true
                    uploading = false
                    b.profileLoading.visibility = View.GONE
                    if (res.value.result == "success") {
                        Toast.makeText(context, getString(R.string.general_save), Toast.LENGTH_SHORT).show()
                        MySharedPreference.getInstance(requireContext()).setUserAvatar("")
                        Glide.with(requireContext())
                                .load(getString(R.string.avatar_url, MySharedPreference.getInstance(requireContext()).getUserAvatar()))
                                .circleCrop()
                                .placeholder(R.drawable.placeholder)
                                .into(b.profileAvatar)
                        b.profileAvatarRemove.visibility = View.VISIBLE
                        setAvatars()
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
                        b.profileSave.isEnabled = true
                        uploading = false
                        b.profileLoading.visibility = View.GONE
                        Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private suspend fun updateProfile(name: String, email: String, bday: String, sex: String, inviteCode: String) {
        when (val res = ApiRepository(RemoteDataSource().getApi(NetworkApi::class.java))
                .updateProfile("Bearer $token", number, name, email, bday, sex, inviteCode)) {
            is Resource.Success -> {
                withContext(Dispatchers.Main) {
                    b.profileSave.isEnabled = true
                    b.profileSave.text = getString(R.string.profile_save)
                    b.profileLoading.visibility = View.GONE
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
                                b.profileInvite.isEnabled = false
                                b.profileInviteLayout.isEnabled = false
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
                        b.profileSave.isEnabled = true
                        b.profileSave.text = getString(R.string.profile_save)
                        b.profileLoading.visibility = View.GONE
                        Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun getViewModel()=ProfileEditViewModel::class.java

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?)= FragmentProfileEditBinding.inflate(inflater,container,false)

}