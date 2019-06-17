package zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ZkClient {

    //连接zookeeper集群地址及端口号
    private final String connectString = "hadoop102:2181,hadoop103:2181,hadoop104:2181";
    //会话超时时间，单位：毫秒
    private final int sessionTimeout = 2000;

    private ZooKeeper zooKeeper = null;

    //1、创建客户端
    @Before
    public void initZk() throws Exception {
          zooKeeper = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                //监听发生后触发的事件
                System.out.println(watchedEvent.getType() + "---" + watchedEvent.getPath());

                //再次注册事件进行监听
                try {
                    zooKeeper.exists("/liuzhoujian", true);
                    zooKeeper.getChildren("/", true);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    //2、创建子节点
    @Test
    public void createNode() throws Exception {
        //第一个参数：节点路径
        //第二个参数：存储的数据
        //第三个参数：访问的权限
        //第四个参数：节点的类型
        String node = zooKeeper.create("/liuzhoujian", "movie.txt".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println(node);
    }

    //3、获取根目录下的节点
    @Test
    public void getNode() throws Exception {
        List<String> children = zooKeeper.getChildren("/", true);
        for(String node : children) {
            System.out.println(node);
        }
        Thread.sleep(Long.MAX_VALUE);
    }

    //4、判断某一路径下的节点是否存在
    @Test
    public void isExist() throws Exception {
        Stat stat = zooKeeper.exists("/liuzhoujian", true); //watch为true表示开启监听，默认只监听一次，需在process回调中再次配置
        System.out.println(stat == null ? "nost exist" : "exist");
        Thread.sleep(Long.MAX_VALUE);
    }
}
