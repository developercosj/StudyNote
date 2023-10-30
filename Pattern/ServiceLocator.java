import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


// ServiceLocator 패턴
// EJB(과거)의 EJB Home 객체나 DB의 Datasource 를 찾을때 (lookup) 응답 시간 감소 가능
// 문제점
// 컴파일 중 오류는 없어도 런타임 오류 발생 가능성 존재 (클래스의 의존성 숨김)
// 어떤 영향을 주는지 확인이 어려움

class ServiceLocator {

    private InitialContext ic;
    private Map cache;
    private static ServiceLocator me;
    static {
        me = new ServiceLocator();
    }

    private ServiceLocator() {
        cache = Collections.synchronizedMap(new HashMap<>());
    }

    public InitialContext getInitialContext() throws Exception {

        try {
            if (ic == null) {
                ic = new InitialContext();
            }
        } catch (Exception e) {
            throw e;
        }
        return ic;

    }

    // home 객체를 찾은 결과를 보관하고 있다가 요청시 메모리에서 찾아서 제공한다.
    // 해당 객체가 cache이름의 map에 없으면 메모리에서 찾는다.
    public static ServiceLocator getInstance() {
        return me;
    }


    //JNDI : Java Naming and Directory Interface -> 디렉터리 서비스에서 제공하는 데이터 및 객체를 발견(discover) 하고 참고(lookup) 하기 위한  자바 API
    // 자바 애플리케이션을 외부 디렉터리 서비스에 연결(주소 데이터 베이스 or LDAP 서버), 자바 애플릿이 호스팅 웹 컨테이너가 제공하는 구성 정보를 참고
    // 사용하려는 DB Pool을 미리 네이밍 한다.
    // ex) Was의 db 정보에 JNDI 설정하면 웹 애플리케이션이 JNDI 를 호출해서 사용 가능
    // 사용법 :
    /*
        // web.xml
            <resource-ref>
                <description>Resource</description>
                <res-ref-name>jdbc/EmployeeDB</res-ref-name>
                <res-type>javax.sql.DataSource</res-type>
                <res-auth>Container</res-auth>
            </resource-ref>

        // server.xml
            <Resource name="jdbc/EmployeeDB" auth="Container" type="javax.sql.DataSource" username="dbusername" password="dbpassword"
                driverClassName="driverName" url="jdbc:oracle:thin..." maxActive="8" maxIdle="4"/>
        // was 코드
             InitialContext initCtx = new InitialContext();
            DataSource ds = (DataSource) initCtx.lookup("java:comp/env");
    */

    public EJBLocalHome getLocalHome(String jndiHomeName) throws Exception {
        EJBLocalHome home = null;
        try {
            if (cache.containsKey(jndiHomeName)){
                home = (EJBLocalHome)cache.get(jndiHomeName);
            } else {
                //lookup :
                home = (EJBLocalHome)getInitialContext().lookup(jndiHomeName);
                // Map에 담아두고 객체 필요시 찾아서 제공
                cache.put(jndiHomeName, home);
            }

        } catch (NamingException ne) {
            throw new Exception(ne.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return home;

    }





}