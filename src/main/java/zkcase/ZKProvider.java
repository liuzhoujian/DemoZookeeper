package zkcase;

import org.apache.zookeeper.*;

/**
 * 服务提供者，向zookeeper注册服务
 */
public class ZKProvider {

    //连接zookeeper集群地址及端口号
    private final String connectString = "hadoop102:2181,hadoop103:2181,hadoop104:2181";
    //会话超时时间，单位：毫秒
    private final int sessionTimeout = 2000;

    private ZooKeeper zooKeeper = null;

    //父节点
    private final String parentNode = "servers";

    //1、获取连接
    public void getConnection() throws Exception {
        zooKeeper = new ZooKeeper(connectString, sessionTimeout,null);
    }

    //2、注册服务器
    public void register(String hostname) throws Exception {
        String createNode = zooKeeper.create("/" + parentNode + "/server", hostname.getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL); //带序号的临时节点，避免节点重名

        System.out.println(hostname + "  is online:" + createNode);
    }


    //3、处理具体的业务
    public void dowork() {
        System.out.println("处理具体的业务...");
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ZKProvider zkServer = new ZKProvider();
        try {
            //1、获取连接
            zkServer.getConnection();
            //2、注册服务器
            zkServer.register(args[0]);
            //3、处理具体的逻辑
            zkServer.dowork();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
