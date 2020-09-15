package jp.techacademy.yoshiyuki.suganuma.qa_app3

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ListView

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_question_detail.*

import java.util.HashMap

class QuestionDetailActivity : AppCompatActivity() {

    private lateinit var mQuestion: Question
    private lateinit var mFavorite: Favorite
    private lateinit var mAdapter: QuestionDetailListAdapter
    private lateinit var mAnswerRef: DatabaseReference

    private lateinit var mAuth: FirebaseAuth

    private val user = FirebaseAuth.getInstance().currentUser

    private var mGenre2: String = ""

    private var mQuestionArrayList = ArrayList<Question>()

    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val map = dataSnapshot.value as Map<String, String>

            val answerUid = dataSnapshot.key ?: ""

            for (answer in mQuestion.answers) {
                // 同じAnswerUidのものが存在しているときは何もしない
                if (answerUid == answer.answerUid) {
                    return
                }
            }

            val body = map["body"] ?: ""
            val name = map["name"] ?: ""
            val uid = map["uid"] ?: ""

            val answer = Answer(body, name, uid, answerUid)
            mQuestion.answers.add(answer)
            mAdapter.notifyDataSetChanged()
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {

        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onCancelled(databaseError: DatabaseError) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_detail)

        // 渡ってきたQuestionのオブジェクトを保持する
        val extras = intent.extras
        mQuestion = extras.get("question") as Question

        title = mQuestion.title

        // ListViewの準備
        mAdapter = QuestionDetailListAdapter(this, mQuestion)
        listView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

        //ログイン判定
        if (user == null) {
            // ログインしていなければお気に入りボタン非表示
            okiniButton.visibility = View.INVISIBLE
        } else {
            // ログインしていればお気に入りボタン表示
            okiniButton.visibility = View.VISIBLE
        }

        okiniButton.setOnClickListener{

            // ログイン済みのユーザーを取得する
            val user = FirebaseAuth.getInstance().currentUser

            val position : Int = 0


            // FirebaseAuthのオブジェクトを取得する
            mAuth = FirebaseAuth.getInstance()

           // val user = mAuth.currentUser

            //var convertView = convertView

            mGenre2 = user!!.uid

            // タイトルを取得する
            //val title = convertView!!.findViewById<View>(R.id.titleTextView) as TextView
            val title = mQuestionArrayList[position].title



            val dataSnapshot: DataSnapshot
            val dataBaseReference = FirebaseDatabase.getInstance().reference

            val genreRef = dataBaseReference.child(FavoritePATH).child(mGenre2)

            val data = HashMap<String, String>()

            //val map = dataSnapshot.value as Map<String, String>


            data["okiniid"] = user.toString()
            data["okininame"] = title
            genreRef.setValue(data)
            genreRef.push().setValue(data, this)

            // 表示名をPrefarenceに保存する
            //saveName(name)

             fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val map = dataSnapshot.value as Map<String, String>

                val answerUid = dataSnapshot.key ?: ""

                for (answer in mQuestion.answers) {
                    // 同じAnswerUidのものが存在しているときは何もしない
                    if (answerUid == answer.answerUid) {
                        return
                    }
                }

                val okiniid = map["okiniid"] ?: ""
                val okininame = map["okininame"] ?: ""
                 
                val favorite = Favorite(okiniid, okininame)
                mFavorite.favorites.add(favorite)
                mAdapter.notifyDataSetChanged()
            }




            // お気に入り
           // data["okiniid"] = FirebaseAuth.getInstance().currentUser!!.okiniid

        //    data["okiniid"] = user

           // data["okininame"] = FirebaseAuth.getInstance().currentUser!!.okininame

            //userRef.setValue(data)



            //genreRef.push().setValue(data, this)

            private fun saveName(name: String) {
                // Preferenceに保存する
                val sp = PreferenceManager.getDefaultSharedPreferences(this)
                val editor = sp.edit()
                editor.putString(NameKEY, name)
                editor.commit()
            }


        }

        fab.setOnClickListener {


            if (user == null) {
                // ログインしていなければログイン画面に遷移させる
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            } else {
                // Questionを渡して回答作成画面を起動する
                // --- ここから ---
                val intent = Intent(applicationContext, AnswerSendActivity::class.java)
                intent.putExtra("question", mQuestion)
                startActivity(intent)
                // --- ここまで ---
            }
        }

        val dataBaseReference = FirebaseDatabase.getInstance().reference
        mAnswerRef = dataBaseReference.child(ContentsPATH).child(mQuestion.genre.toString()).child(mQuestion.questionUid).child(AnswersPATH)
        mAnswerRef.addChildEventListener(mEventListener)
    }
}