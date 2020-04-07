package test;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MongoTest {
    private  MongoClient mongoClient;
    private MongoCollection<Document> comment;
    @Before
    public void init(){
        mongoClient = new MongoClient("deverourw.vicp.net");
        MongoDatabase conmentdb = mongoClient.getDatabase("commentdb");
        comment = conmentdb.getCollection("comment");
    }

    @Test
    public void test1(){
        FindIterable<Document> documents = comment.find();
        for (Document document:documents){
            System.out.println("-------------------");
            System.out.print("_id:"+document.get("_id")+"      ");
            System.out.print("content:"+document.get("content")+"      ");
            System.out.print("userid:"+document.get("userid")+"      ");
            System.out.println("thumbup:"+document.get("thumbup"));
        }
    }

    @Test
    public void test2(){
        BasicDBObject bson = new BasicDBObject("_id","1");

        FindIterable<Document> documents = comment.find(bson);
        for (Document document:documents){
            System.out.println("-------------------");
            System.out.print("_id:"+document.get("_id")+"      ");
            System.out.print("content:"+document.get("content")+"      ");
            System.out.print("userid:"+document.get("userid")+"      ");
            System.out.println("thumbup:"+document.get("thumbup"));
        }

    }

    @Test
    public void test3(){
        Map<String, Object> map = new HashMap<>();
        map.put("_id","6");
        map.put("content","新增测试");
        map.put("userid","1019");
        map.put("thumbup","666");
        Document document = new Document(map);
        comment.insertOne(document);
    }

    @Test
    public void test4(){
        BasicDBObject filter = new BasicDBObject("_id","6");
        BasicDBObject update = new BasicDBObject("$set",new Document("userid","999") );
        comment.updateOne(filter,update);
    }


    @Test
    public void test5(){
        BasicDBObject bson = new BasicDBObject("_id","6" );
        comment.deleteMany(bson);
    }

    @After
    public void after(){
        mongoClient.close();
    }

}
