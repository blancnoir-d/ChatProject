package me.saeha.android.chatproject.ui.message
import android.annotation.SuppressLint
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import me.saeha.android.chatproject.R
import me.saeha.android.chatproject.databinding.ItemMessageBinding
import me.saeha.android.chatproject.model.ChattingRoom
import me.saeha.android.chatproject.ui.chatting.ChattingActivity


class MessagesListAdapter (val context: Context?, private var chattingRoomList:List<ChattingRoom>):
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
            intent.putExtra("whereFrom", 2)
            intent.putExtra("chatRoomData",item)
          	context?.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = chattingRoomList.size


    class ItemViewHolder(binding: ItemMessageBinding): RecyclerView.ViewHolder(binding.root){
        val ivMessageProfileImage = binding.ivMessagesProfileImage
        val tvMessageName = binding.tvMessagesName
        val tvMessagePosition = binding.tvMessagesPosition
        val tvMessageLastMessage = binding.tvMessagesLastMessage
        val tvMessageLastDay = binding.tvMessagesLastDay
        val tvMessageUnseenMessage = binding.tvMessagesUnseenMessage

        fun onBind(context: Context?, item: ChattingRoom){
            if (context != null) {
//                Glide.with(context)
//                    .load(item.partnerImage)
//                    .override(50,50)
//                    .centerCrop()
//                    .into(ivMessageProfileImage)

                Glide.with(context)
                    .load(R.drawable.ic_user)
                    .override(110, 110)
                    .into(ivMessageProfileImage)
            }


            tvMessageName.text = item.partnerName
            tvMessagePosition.text = item.partnerPosition
            tvMessageLastMessage.text = item.lastMessage
            tvMessageLastDay.text = item.lastDate?.substring(0,10)

            if(item.unSeenMessage == 0){
                tvMessageUnseenMessage.visibility = View.GONE
            }else{
                tvMessageUnseenMessage.visibility = View.VISIBLE
                tvMessageUnseenMessage.text = item.unSeenMessage.toString()
            }
        }
    }

}