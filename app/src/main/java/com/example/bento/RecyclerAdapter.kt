package com.example.bento

/**
 * Created by Angel on 3/13/2019.
 */
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.user_item.view.*

class RecyclerAdapter(
    var activity: Activity,
    var userList: List<User>
) : RecyclerView.Adapter<ViewHolderClick>() {

    override fun getItemCount() = userList.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClick =
        ViewHolderClick(LayoutInflater.from(activity).inflate(R.layout.user_item, parent, false))

    override fun onBindViewHolder(holder: ViewHolderClick, position: Int) =
        holder.bind(userList.sortedWith(compareByDescending { it.score })[position])
}

class ViewHolderClick(view: View) : RecyclerView.ViewHolder(view) {
    var name: TextView = view.userName
    var score: TextView = view.userScore

    fun bind(item: User) {
        name.text = item.name
        score.text = item.score.toString()
    }
}
