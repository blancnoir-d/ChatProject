package me.saeha.android.chatproject.ui.message
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import com.bumptech.glide.Glide
import me.saeha.android.chatproject.R
import me.saeha.android.chatproject.databinding.ItemMessageBinding
import me.saeha.android.chatproject.model.ChattingRoom
import me.saeha.android.chatproject.ui.chatting.ChattingActivity


class MessagesListAdapter (val context: Context, private val chattingRoomList:List<ChattingRoom>):
RecyclerView.Adapter<MessagesListAdapter.ItemViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        //viewbinding - item 레이아웃과
        val templateDetailBinding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return ItemViewHolder(templateDetailBinding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = chattingRoomList[position]
        holder.onBind(context, item)

    	//아이템 클릭리스너
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChattingActivity::class.java)
          	context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = chattingRoomList.size

    class ItemViewHolder(binding: ItemMessageBinding): RecyclerView.ViewHolder(binding.root){
        val ivMessageProfileImage = binding.ivMessagesProfileImage
        val tvMessageName = binding.tvMessagesName
        val tvMessagePosition = binding.tvMessagesPosition
        val tvMessageRecentMessage = binding.tvMessagesRecentMessage
        val tvMessageLastDay = binding.tvMessagesLastDay
        val tvMessageUnseenMessage = binding.tvMessagesUnseenMessage

        fun onBind(context: Context, item: ChattingRoom){
            //영화 포스터 이미지 set
            Glide.with(context)
                .load(item.partnerImage)
                .override(190,250)
                .centerCrop()
                .into(ivMessageProfileImage)

        }
    }
}