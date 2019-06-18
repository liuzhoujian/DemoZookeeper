package zkclient;


import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * 高级API ZkClient的使用, 实现了session超时自动重连、watcher反复注册等，减轻开发人员的负担
 */
public class ZkClientDemo {

    private final String servers = "hadoop102:2181,hadoop103:2181,hadoop104:2181";

    private final int connectionTimeout = 2000; //ms

    private ZkClient zkClient = null;

    private final String path = "/zk-data";

    @Before
    public void initZK() {
        zkClient = new ZkClient(servers, connectionTimeout);

        //注册监听器，监听path下子节点数据的变化
        zkClient.subscribeDataChanges(path, new IZkDataListener() {
            public void handleDataChange(String dataPath, Object data) throws Exception {
                System.out.println("handleDataChange, dataPath:" + dataPath + ", data:" + data);
            }

            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println("handleDataDeleted, dataPath:" + dataPath);
            }
        });

        //注册监听器，监听path下子节点的变化
        zkClient.subscribeChildChanges(path, new IZkChildListener() {
            public void handleChildChange(String dataPath, List<String> children) throws Exception {
                System.out.println("handleChildChange, " + "dataPath:" + dataPath + "->" + children);
            }
        });
    }

    @Test
    public void createNode() {
        //若节点已存在，则删除
        if(zkClient.exists(path)) {
           zkClient.delete(path);
        }

        //创建持久节点,并写入数据
        zkClient.createPersistent(path, "test-data");
    }


    @Test
    public void readData() {
        String data = zkClient.readData(path);
        System.out.println(data);
    }

    @Test
    public void updateData() {
        zkClient.writeData(path, "test-data1");
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //结果：handleDataChange, dataPath:/zk-data, data：test-data1


    @Test
    public void deleteNode() {
        zkClient.delete(path);
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //结果：handleDataDeleted, dataPath:/zk-data
}
