package com01.qqService;

import com01.QQcommon.Message;
import com01.QQcommon.MessageType;

import javax.swing.text.html.ObjectView;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

public class ServiceThread extends Thread {
    private Socket socket;
    private String userId;

    public ServiceThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        //通过socket对象 实现读取和输入
        while (true) {
            try {
                //总起
                System.out.println("服务器和 " + userId + " 客户端保持通信..");
                //读取信息
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) objectInputStream.readObject();


                if (message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)) {
                    //
                    System.out.println(message.getSender() + " 在线用户列表");
                    String onlineList = ManageServiceThread.getOnlineList();
                    //构建message对象 返回给客户端
                    Message message1 = new Message();
                    message1.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                    message1.setContent(onlineList);
                    message1.setGetter(message.getSender());//给的是sender 返回去是getter
                    // 返回这个message对象
                    // 对应的socket对应的流
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
                    objectOutputStream.writeObject(message1);

                } else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)) {
                    //服务端拿到 getter  就是要发的客户端
                    ServiceThread serviceThread = ManageServiceThread.get(message.getGetter());
                    //发给对应的客户端
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(serviceThread.getSocket().getOutputStream());
                    objectOutputStream.writeObject(message);

                } else if (message.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)) {//客户端退出
                    System.out.println(message.getSender() + " 退出");
                    //删除对应线程
                    ManageServiceThread.remove(message.getSender());
                    socket.close();
                    break;//退出run方法
                } else if (message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)) {
                    HashMap<String, ServiceThread> hashMap = ManageServiceThread.getHashMap();
                    Iterator<String> iterator = hashMap.keySet().iterator();
                    while (iterator.hasNext()) {
                        //取出用户
                        String userId = iterator.next().toString();
                        // 当是循环读取的不要加this
                        if (!userId.equals(message.getSender())) {//用户集合里不包含sender
                            //发给所有的客户端
                            ObjectOutputStream objectOutputStream =
                                    new ObjectOutputStream(hashMap.get(userId).getSocket().getOutputStream());
                            objectOutputStream.writeObject(message);
                        }
                    }
                }else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES)){
                    ObjectOutputStream objectOutputStream =
                            new ObjectOutputStream(ManageServiceThread.get
                                    (message.getGetter()).getSocket().getOutputStream());
                    objectOutputStream.writeObject(message);
                }else if (message.getMesType().equals(MessageType.MESSAGE_LOGIN_FAIL)){

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
