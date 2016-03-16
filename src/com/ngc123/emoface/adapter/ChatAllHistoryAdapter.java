/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ngc123.emoface.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatRoom;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.DateUtils;
import com.easemob.util.EMLog;
import com.ngc123.emoface.Constant;
import com.ngc123.emoface.DemoApplication;
import com.ngc123.emoface.R;
import com.ngc123.emoface.activity.ChatActivity;
import com.ngc123.emoface.activity.MainActivity;
import com.ngc123.emoface.db.InviteMessgeDao;
import com.ngc123.emoface.reminder.GooViewListener;
import com.ngc123.emoface.swipe.SwipeLayout;
import com.ngc123.emoface.swipe.SwipeLayout.SwipeListener;
import com.ngc123.emoface.utils.SmileUtils;
import com.ngc123.emoface.utils.UserUtils;
import com.ngc123.emoface.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;



/**
 * 显示所有聊天记录adpater
 * 
 */
public class ChatAllHistoryAdapter extends ArrayAdapter<EMConversation> {

	private static final String TAG = "ChatAllHistoryAdapter";
	private LayoutInflater inflater;
	private List<EMConversation> conversationList;
	private List<EMConversation> copyConversationList;
	private HashSet<Integer> mRemoved = new HashSet<Integer>();
	private HashSet<SwipeLayout> mUnClosedLayouts = new HashSet<SwipeLayout>();
	private ConversationFilter conversationFilter;
    private boolean notiyfyByFilter;
    private Context context;
    private EMConversation conversation;

	public ChatAllHistoryAdapter(Context context, int textViewResourceId, List<EMConversation> objects) {
		super(context, textViewResourceId, objects);
		this.conversationList = objects;
		this.context = context;
		copyConversationList = new ArrayList<EMConversation>();
		copyConversationList.addAll(objects);
		inflater = LayoutInflater.from(context);
	}

    @Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row_chat_history, parent, false);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if (holder == null) {
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.unreadLabel = (TextView) convertView.findViewById(R.id.unread_msg_number);
			holder.message = (TextView) convertView.findViewById(R.id.message);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
			holder.msgState = convertView.findViewById(R.id.msg_state);
			holder.mButtonCall = (Button) convertView.findViewById(R.id.bt_call);
            holder.mButtonDel = (Button) convertView.findViewById(R.id.bt_delete);
			holder.list_item_layout = (SwipeLayout) convertView.findViewById(R.id.list_item_layout);
			convertView.setTag(holder);
		}
		
		SwipeLayout view = (SwipeLayout) convertView;
        
        view.close(false, false);
        
        
        view.getFrontView().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                conversation = getItem(position);
                String username = conversation.getUserName();
                Intent intent;
                if (username.equals(DemoApplication.getInstance().getUserName())){
                    
                }
                else {
                    // 进入聊天页面
                    intent = new Intent();
                    intent.setClass(context, ChatActivity.class); 
                    if(conversation.isGroup()){
                        if(conversation.getType() == EMConversationType.ChatRoom){
                         // it is group chat
                            intent.putExtra("chatType", ChatActivity.CHATTYPE_CHATROOM);
                            intent.putExtra("groupId", username);
                        }else{
                         // it is group chat
                            intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                            intent.putExtra("groupId", username);
                        }
                        
                    }else{
                        // it is single chat
                        intent.putExtra("userId", username);
                    }
//                    startActivity(intent);
                    context.startActivity(intent);
                }
            }
        });
		
        view.setSwipeListener(mSwipeListener);
        
        holder.mButtonCall.setTag(position);
        holder.mButtonCall.setOnClickListener(onActionClick);

        holder.mButtonDel.setTag(position);
        holder.mButtonDel.setOnClickListener(onActionClick);
        
        TextView mUnreadView = holder.unreadLabel;
        
        boolean visiable = !mRemoved.contains(position);
        mUnreadView.setVisibility(visiable ? View.VISIBLE : View.GONE);

        if (visiable) {
            mUnreadView.setText(String.valueOf(conversationList.get(position).getUnreadMsgCount()));
            mUnreadView.setTag(conversationList.get(position).getUnreadMsgCount());
            GooViewListener mGooListener = new GooViewListener(context, mUnreadView) {
                @Override
                public void onDisappear(PointF mDragCenter) {
                    super.onDisappear(mDragCenter);

                    mRemoved.add(position);
                    notifyDataSetChanged();
                    Utils.showToast(context,
                            "Cheers! We have get rid of it!");
                    conversationList.get(position).markAllMessagesAsRead();
                    // 更新消息未读数
                    ((MainActivity) context).updateUnreadLabel();
                }

                @Override
                public void onReset(boolean isOutOfRange) {
                    super.onReset(isOutOfRange);

                    notifyDataSetChanged();
                    Utils.showToast(context,
                            isOutOfRange ? "Are you regret?" : "Try again!");
                }
            };
            mUnreadView.setOnTouchListener(mGooListener);
        }
        
        
        if (position % 2 == 0) {
            holder.list_item_layout.setBackgroundResource(R.drawable.mm_listitem);
        } else {
            holder.list_item_layout.setBackgroundResource(R.drawable.mm_listitem_grey);
        }

        // 获取与此用户/群组的会话
        conversation = getItem(position);
        // 获取用户username或者群组groupid
        String username = conversation.getUserName();
        if (conversation.getType() == EMConversationType.GroupChat) {
            // 群聊消息，显示群聊头像
            holder.avatar.setImageResource(R.drawable.group_icon);
            EMGroup group = EMGroupManager.getInstance().getGroup(username);
            holder.name.setText(group != null ? group.getGroupName() : username);
        } else if(conversation.getType() == EMConversationType.ChatRoom){
            holder.avatar.setImageResource(R.drawable.group_icon);
            EMChatRoom room = EMChatManager.getInstance().getChatRoom(username);
            holder.name.setText(room != null && !TextUtils.isEmpty(room.getName()) ? room.getName() : username);
        }else {
            UserUtils.setUserAvatar(getContext(), username, holder.avatar);
            if (username.equals(Constant.GROUP_USERNAME)) {
                holder.name.setText("群聊");

            } else if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
                holder.name.setText("申请与通知");
            }
            holder.name.setText(username);
        }

        if (conversation.getUnreadMsgCount() > 0) {
            // 显示与此用户的消息未读数
            mUnreadView.setText(String.valueOf(conversation.getUnreadMsgCount()));
            mUnreadView.setVisibility(View.VISIBLE);
        } else {
            mUnreadView.setVisibility(View.INVISIBLE);
        }

        if (conversation.getMsgCount() != 0) {
            // 把最后一条消息的内容作为item的message内容
            EMMessage lastMessage = conversation.getLastMessage();
            holder.message.setText(SmileUtils.getSmiledText(getContext(), getMessageDigest(lastMessage, (this.getContext()))),
                    BufferType.SPANNABLE);

            holder.time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
            if (lastMessage.direct == EMMessage.Direct.SEND && lastMessage.status == EMMessage.Status.FAIL) {
                holder.msgState.setVisibility(View.VISIBLE);
            } else {
                holder.msgState.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

//        return view;
//    }

    OnClickListener onActionClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Integer p = (Integer) v.getTag();
            int id = v.getId();
            if (id == R.id.bt_call) {
                closeAllLayout();
                Utils.showToast(context, "position: " + p + " call");
            } else if (id == R.id.bt_delete) {
                closeAllLayout();
                Utils.showToast(context, "position: " + p + " del");
                // 删除此会话
                EMChatManager.getInstance().deleteConversation(conversation.getUserName(), conversation.isGroup(), false);
                InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(context);
                inviteMessgeDao.deleteMessage(conversation.getUserName());
                remove(conversation);
                notifyDataSetChanged();

                // 更新消息未读数
                ((MainActivity) context).updateUnreadLabel();
            }
        }
    };
    SwipeListener mSwipeListener = new SwipeListener() {
        @Override
        public void onOpen(SwipeLayout swipeLayout) {
//          Utils.showToast(mContext, "onOpen");
            mUnClosedLayouts.add(swipeLayout);
        }

        @Override
        public void onClose(SwipeLayout swipeLayout) {
//          Utils.showToast(mContext, "onClose");
            mUnClosedLayouts.remove(swipeLayout);
        }

        @Override
        public void onStartClose(SwipeLayout swipeLayout) {
//          Utils.showToast(mContext, "onStartClose");
        }

        @Override
        public void onStartOpen(SwipeLayout swipeLayout) {
//          Utils.showToast(mContext, "onStartOpen");
            closeAllLayout();
            mUnClosedLayouts.add(swipeLayout);
        }

    };
    public int getUnClosedCount(){
        return mUnClosedLayouts.size();
    }
    
    protected Context getActivity() {
        // TODO Auto-generated method stub
        return null;
    }

    public void closeAllLayout() {
        if(mUnClosedLayouts.size() == 0)
            return;
        
        for (SwipeLayout l : mUnClosedLayouts) {
            l.close(true, false);
        }
        mUnClosedLayouts.clear();
    }
      

	/**
	 * 根据消息内容和消息类型获取消息内容提示
	 * 
	 * @param message
	 * @param context
	 * @return
	 */
	private String getMessageDigest(EMMessage message, Context context) {
		String digest = "";
		switch (message.getType()) {
		case LOCATION: // 位置消息
			if (message.direct == EMMessage.Direct.RECEIVE) {
				// 从sdk中提到了ui中，使用更简单不犯错的获取string的方法
				// digest = EasyUtils.getAppResourceString(context,
				// "location_recv");
				digest = getStrng(context, R.string.location_recv);
				digest = String.format(digest, message.getFrom());
				return digest;
			} else {
				// digest = EasyUtils.getAppResourceString(context,
				// "location_prefix");
				digest = getStrng(context, R.string.location_prefix);
			}
			break;
		case IMAGE: // 图片消息
			ImageMessageBody imageBody = (ImageMessageBody) message.getBody();
			digest = getStrng(context, R.string.picture) + imageBody.getFileName();
			break;
		case VOICE:// 语音消息
			digest = getStrng(context, R.string.voice);
			break;
		case VIDEO: // 视频消息
			digest = getStrng(context, R.string.video);
			break;
		case TXT: // 文本消息
			if(!message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL,false)){
				TextMessageBody txtBody = (TextMessageBody) message.getBody();
				digest = txtBody.getMessage();
			}else{
				TextMessageBody txtBody = (TextMessageBody) message.getBody();
				digest = getStrng(context, R.string.voice_call) + txtBody.getMessage();
			}
			break;
		case FILE: // 普通文件消息
			digest = getStrng(context, R.string.file);
			break;
		default:
			EMLog.e(TAG, "unknow type");
			return "";
		}

		return digest;
	}

	private static class ViewHolder {
		/** 和谁的聊天记录 */
		TextView name;
		/** 消息未读数 */
		TextView unreadLabel;
		/** 最后一条消息的内容 */
		TextView message;
		/** 最后一条消息的时间 */
		TextView time;
		/** 用户头像 */
		ImageView avatar;
		/** 最后一条消息的发送状态 */
		View msgState;
		/** 整个list中每一行总布局 */
		SwipeLayout list_item_layout;
		/** 打电话 */
		Button mButtonCall;
		/** 删除消息 */
        Button mButtonDel;

	}

	String getStrng(Context context, int resId) {
		return context.getResources().getString(resId);
	}
	
	

	@Override
	public Filter getFilter() {
		if (conversationFilter == null) {
			conversationFilter = new ConversationFilter(conversationList);
		}
		return conversationFilter;
	}
	
	private class ConversationFilter extends Filter {
		List<EMConversation> mOriginalValues = null;

		public ConversationFilter(List<EMConversation> mList) {
			mOriginalValues = mList;
		}

		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();

			if (mOriginalValues == null) {
				mOriginalValues = new ArrayList<EMConversation>();
			}
			if (prefix == null || prefix.length() == 0) {
				results.values = copyConversationList;
				results.count = copyConversationList.size();
			} else {
				String prefixString = prefix.toString();
				final int count = mOriginalValues.size();
				final ArrayList<EMConversation> newValues = new ArrayList<EMConversation>();

				for (int i = 0; i < count; i++) {
					final EMConversation value = mOriginalValues.get(i);
					String username = value.getUserName();
					
					EMGroup group = EMGroupManager.getInstance().getGroup(username);
					if(group != null){
						username = group.getGroupName();
					}

					// First match against the whole ,non-splitted value
					if (username.startsWith(prefixString)) {
						newValues.add(value);
					} else{
						  final String[] words = username.split(" ");
	                        final int wordCount = words.length;

	                        // Start at index 0, in case valueText starts with space(s)
	                        for (int k = 0; k < wordCount; k++) {
	                            if (words[k].startsWith(prefixString)) {
	                                newValues.add(value);
	                                break;
	                            }
	                        }
					}
				}

				results.values = newValues;
				results.count = newValues.size();
			}
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			conversationList.clear();
			conversationList.addAll((List<EMConversation>) results.values);
			if (results.count > 0) {
			    notiyfyByFilter = true;
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}

		}

	}
	
	@Override
	public void notifyDataSetChanged() {
	    super.notifyDataSetChanged();
	    if(!notiyfyByFilter){
            copyConversationList.clear();
            copyConversationList.addAll(conversationList);
            notiyfyByFilter = false;
        }
	}
	
}
