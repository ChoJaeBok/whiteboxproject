package gst.study.yellowtv;

public class IpReceiver {
//스트리밍할 라즈베리파이와 서버인 B3의 아이피를 받아주는 공간.
    private String name = "";
    private String ip1 = "";
    private String ip2 ="";
    private String b3_ip ="";//B3아이피 받는구간., 수정



    private String b3_msg = "";
    public IpReceiver(){
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp1() {
        return ip1;
    }

    public void setIp1(String ip1) {
        this.ip1 = ip1;
    }

    public String getIp2() {
        return ip2;
    }

    public void setIp2(String ip2) {
        this.ip2 = ip2;
    }

    public String getB3_ip() { return b3_ip;    }//수정

    public void setB3_ip(String b3_ip) { this.b3_ip = b3_ip;    }//수정

    public String getB3_msg() { return b3_msg;    }

    public void setB3_msg(String b3_msg) {   this.b3_msg = b3_msg;    }

}
