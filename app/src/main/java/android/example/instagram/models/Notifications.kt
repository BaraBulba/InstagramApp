package android.example.instagram.models

class Notifications {
    private var userID: String = ""
    private var text: String = ""
    private var postID: String = ""
    private var isPost: Boolean = false

    constructor()

    constructor(userID: String,text: String,postID:String,isPost:Boolean) {
        this.userID = userID
        this.text = text
        this.postID = postID
        this.isPost = isPost
    }

    fun getPostId():String{
        return postID
    }

    fun getUserId():String{
        return userID
    }
    fun getText():String{
        return text
    }
    fun getIsPost():Boolean{
        return isPost
    }

    fun setPostId(postID: String){
        this.postID= postID
    }

    fun setUserId(userID: String){
        this.userID= userID
    }

    fun setText(text: String){
        this.text= text
    }

    fun setIsPost(isPost: Boolean){
        this.isPost= isPost
    }

}