package android.example.instagram.models

class Users {
    private var userName: String = ""
    private var bio: String = ""
    private var fullName: String = ""
    private var image: String = ""
    private var uid: String = ""
    private var email: String = ""
    private var website: String = ""

    constructor()

    constructor(userName: String,
                bio: String,
                fullName: String,
                image: String,
                uid: String,
                email: String,
                website: String) {
        this.userName = userName
        this.bio = bio
        this.fullName = fullName
        this.image = image
        this.uid = uid
        this.email = email
        this.website = website
    }

    fun getWebSite(): String{
        return website
    }
    fun setWebSite(website: String){
        this.website = website
    }

    fun getUserName(): String{
        return userName
    }
    fun setUserName(userName: String){
        this.userName = userName
    }

    fun getFullName(): String{
        return fullName
    }
    fun setFullName(fullName: String){
        this.fullName = fullName
    }

    fun getBio(): String{
        return bio
    }
    fun setBio(bio: String){
        this.bio = bio
    }

    fun getUid(): String{
        return uid
    }
    fun setUid(uid: String){
        this.uid = uid
    }

    fun getImage(): String{
        return image
    }
    fun setImage(image: String){
        this.image = image
    }

    fun getEmail(): String{
        return email
    }
    fun setEmail(email: String){
        this.email = email
    }
}