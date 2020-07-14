package ir.ghararemaghzha.game.models;

import com.google.gson.annotations.SerializedName;

public class VerifyResponse {
//    "result": "success",
//            "version": "1",
//            "version_essential": "0",
//            "message": "ok"

    private String result;
    private String version;
    @SerializedName("version_essential")
    private String versionEssential;
    private String message;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersionEssential() {
        return versionEssential;
    }

    public void setVersionEssential(String versionEssential) {
        this.versionEssential = versionEssential;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
