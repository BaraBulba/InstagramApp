package android.example.instagram.models

class Notifications {
    private var userId: String = ""
    private var text: String = ""
    private var postId: String = ""
    private var isPost: Boolean = false

    constructor()

    constructor(userId: String,text: String,postId:String,isPost:Boolean) {
        this.userId = userId
        this.text = text
        this.postId = postId
        this.isPost = isPost
    }

    fun getPostId():String{
        return postId
    }

    fun getUserId():String{
        return userId
    }
    fun getText():String{
        return text
    }
    fun getIsPost():Boolean{
        return isPost
    }

    fun setPostId(postId: String){
        this.postId= postId
    }

    fun setUserId(userId: String){
        this.userId= userId
    }

    fun setText(text: String){
        this.text= text
    }

    fun setIsPost(isPost: Boolean){
        this.isPost= isPost
    }

}