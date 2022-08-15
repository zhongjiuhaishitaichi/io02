package com01.qqService;

import com01.QQcommon.Message;
import com01.QQcommon.MessageType;
import com01.QQcommon.User;

import javax.lang.model.element.NestingKind;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


public class Service_ {
    private static ServerSocket serverSocket = null;
    private static HashMap<String,User> validUsers=new HashMap<>();
  /*  private static ConcurrentHashMap<String, Message> concurrentHashMap=new ConcurrentHashMap<>();*/
    static {
        validUsers.put("时文宣",new User("时文宣","123456"));
        validUsers.put("孙祥宇",new User("孙祥宇","123456"));
        validUsers.put("小蓝",new User("小蓝","123456"));
        validUsers.put("小红",new User("小红","123456"));
        validUsers.put("林丰雨",new User("林丰雨","123456"));
    }

   /* public  void  OffLineUser () throws IOException, ClassNotFoundException {

        while(true){
            Socket accept = serverSocket.accept();
            ObjectInputStream objectInputStream = new ObjectInputStream(accept.getInputStream());
           Message message = (Message) objectInputStream.readObject();
            Iterator<String> iterator = validUsers.keySet().iterator();
            while (iterator.hasNext()) {
                String userName=iterator.next().toString();
                if (!checkUser(userName,"123456")){
           concurrentHashMap.put(userName,message);

                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(accept.getOutputStream());
                    objectOutputStream.writeObject(message);
                    //创建线程 保持和客户端的通信 该线程需要有 socket对象
                    message.setMesType(MessageType.MESSAGE_FILE_MES);
                    ServiceThread serviceThread = new ServiceThread(accept,userName);
                    serviceThread.start();
                    //线程放入集合
                    ManageServiceThread.add(userName, serviceThread);
                }else{
                    concurrentHashMap.remove(userName,message);
                }
            }
        }
    }
*/

    private static boolean checkUser(String userId, String pwd){
        //通过get value  判断获取对象  判断输入的userId和 pwd 是否和该对象一样
        User User = validUsers.get(userId);
        if (User==null){
            return false;
        }
      if (!(User.getPwd().equals(pwd)))
        return false;
      return true;
    }
    public Service_() {
        try {
            System.out.println("服务器在9999端口监听");
            //启动推送线程
            new Thread(new SendNewsToAll()).start();

            serverSocket = new ServerSocket(9999);
            while (true) {//监听是一直进行的  一直保持监听状态 所以while
                Socket socket = serverSocket.accept();
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                User user = (User) objectInputStream.readObject();
                //socket 相应的输出流
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                //传输的massage 对象
                Message message = new Message();

                //验证用户
                if (checkUser(user.getUseId(),user.getPwd())) {
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    objectOutputStream.writeObject(message);

                    //创建线程 保持和客户端的通信 该线程需要有 socket对象
                    ServiceThread serviceThread = new ServiceThread(socket, user.getUseId());
                    serviceThread.start();

                    //线程放入集合
                    ManageServiceThread.add(user.getUseId(), serviceThread);
                } else {//登陆失败返回

                    message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    objectOutputStream.writeObject(message);

                    socket.close();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally{
            //如果 服务端退出while循环 不再监听 退出
            try {
                serverSocket.close();
            } catch (IOException e) {
                    throw new RuntimeException(e);
            }
        }
    }

}
