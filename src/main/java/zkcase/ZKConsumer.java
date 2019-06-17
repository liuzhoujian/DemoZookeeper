package zkcase;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务消费者，从zk中获取服务器列表，进行消费
 */
public class ZKConsumer {

    //连接zookeeper集群地址及端口号
    private final String connectString = "hadoop102:2181,hadoop103:2181,hadoop104:2181";
    //会话超时时间，单位：毫秒
    private final int sessionTimeout = 2000;

    private ZooKeeper zooKeeper = null;

    //父节点
    private final String parentNode = "servers";

    //1、获取连接
    public void getConnection() throws Exception {
        zooKeeper = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                System.out.println(watchedEvent.getType() + "----------" + watchedEvent.getPath());

                //再次注册监听
                try {
                    getServers();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //2、获取服务器列表
    public void getServers() throws Exception {
        List<String> children = zooKeeper.getChildren("/" + parentNode, true);//添加监听事件

        List<String> serverList = new ArrayList<String>();
        for(String child : children) {
            byte[] server = zooKeeper.getData("/" + parentNode + "/" + child, false, null);
            serverList.add(new String(server));
        }

        System.out.println(serverList);
    }


    //3、具体的业务
    public void dowork() {
        System.out.println("请求某个业务...");
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ZKConsumer zkConsumer = new ZKConsumer();
        try {
            //1、获取zk连接
            zkConsumer.getConnection();
            //2、注册监听事件，获取服务器列表
            zkConsumer.getServers();
            //3、执行具体的业务逻辑
            zkConsumer.dowork();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
