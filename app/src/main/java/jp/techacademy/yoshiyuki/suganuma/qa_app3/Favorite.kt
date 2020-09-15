package jp.techacademy.yoshiyuki.suganuma.qa_app3
import java.io.Serializable
import java.util.ArrayList

class Favorite(val okiniid: String, val okininame: String, val favorites: ArrayList<Answer>) : Serializable