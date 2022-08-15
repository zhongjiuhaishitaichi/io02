package com01.qqService;

import com01.QQcommon.Message;
import com01.QQcommon.MessageType;
import com01.Utils.Utils;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Iterator;

public class SendNewsToAll implements Runnable {
    @Override
    public void run() {
        while (true) {
            System.out.println("请输入服务器要推送的新闻: ");
            String news = Utils.readString(100);

            if ("exit".equals(news)) {
                break;
            }

            Message message = new Message();
            message.setSender("服务器");
            message.setContent(news);
            message.setMesType(MessageType.MESSAGE_TO_ALL_MES);
            message.setSendTime(new Date().toString());
            Iterator<String> iterator = ManageServiceThread.getHashMap().keySet().iterator();
            while (iterator.hasNext()) { // k - v   key-->用户名  value-->socket线程
                String onlineUserId = iterator.next().toString();
                try {
                    ObjectOutputStream objectOutputStream =
                            new ObjectOutputStream(ManageServiceThread.get(onlineUserId).getSocket().getOutputStream());
                    objectOutputStream.writeObject(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
