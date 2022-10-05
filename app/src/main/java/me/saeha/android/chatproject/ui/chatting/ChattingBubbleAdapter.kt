package me.saeha.android.chatproject.ui.chatting

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.saeha.android.chatproject.R
import me.saeha.android.chatproject.databinding.ItemChatbubbleLeftBinding
import me.saeha.android.chatproject.databinding.ItemChatbublleRightBinding
import me.saeha.android.chatproject.databinding.ItemChattingDateBinding
import me.saeha.android.chatproject.getUserId
import me.saeha.android.chatproject.model.Message

class ChattingBubbleAdapter(val context: Context, private val chattingLog: List<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //ViewHolder 타입
    private val typeRight = 0
    private val typeLeft = 1
    private val typeCenter = 2

    val userId = getUserId(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //RecyclerView ViewBinding 사용
        val rightBinding =
            ItemChatbublleRightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val leftBinding =
            ItemChatbubbleLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val chatDateBinding =
            ItemChattingDateBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        val holder: RecyclerView.ViewHolder = when(viewType){
            typeRight -> {
                RightViewHolder(rightBinding)
            }
            typeLeft -> {
                LeftViewHolder(leftBinding)
            }
            else -> {
                CenterViewHolder(chatDateBinding)
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = chattingLog[position]
        when(holder){
           is RightViewHolder -> {
               holder.onRightBind(context, item)
            }
            is LeftViewHolder -> {
                holder.onLeftBind(context, item)
            }
            else -> {
                holder as CenterViewHolder
                holder.onDateBind(context, item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(chattingLog[position].senderId){
            userId -> {
                typeRight
            }
            "C" -> {
                typeCenter
            }
            else -> {
                typeLeft
            }
        }

    }

    override fun getItemCount() = chattingLog.size

    //내가 보낸 메시지
    class RightViewHolder(private val binding: ItemChatbublleRightBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val timeStamp = binding.tvChattingTimestampMe
        private val sendMessage = binding.tvChattingMessageMe
        fun onRightBind(context: Context, message: Message) {
            sendMessage.text = message.content
            timeStamp.text = message.created?.substring(11, 19)

        }
    }

    //상대방 메시지
    class LeftViewHolder(private val binding: ItemChatbubbleLeftBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val timeStamp = binding.tvChattingTimestampOther
        private val sendMessage = binding.tvChattingMessageOther

        fun onLeftBind(context: Context, message: Message) {
            sendMessage.text = message.content
            timeStamp.text = message.created?.substring(11, 19)


        }
    }

    class CenterViewHolder(private val binding: ItemChattingDateBinding) : RecyclerView.ViewHolder(binding.root){
        private val tvChattingDate = binding.tvChattingDate

        fun onDateBind(context: Context, chatLog: Message){
            tvChattingDate.text = chatLog.created?.substring(0,10)

        }
    }


}