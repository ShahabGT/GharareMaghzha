package ir.ghararemaghzha.game.models

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ContactsModel(
        @PrimaryKey var contactId: Int=0,
        @SerializedName("user_id") var id: String="",
        @SerializedName("user_number") var number: String="",
        @SerializedName("user_code") var code: String="",
        @SerializedName("user_name") var name: String="",
        @SerializedName("user_avatar") var avatar: String="",
        @SerializedName("user_reg_date") var regDate: String="",
         var type: Int=-1,
):RealmObject()
