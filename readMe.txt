################
#. batchSample 설명서
################

1. 개요
	가. 별도의 컨테이너를 이용하지 않는 stand alone형 배치 프로그램이다.
	나. 스케줄링은 젠킨스나 크론과 같은 별도의 프로그램을 이용한다.

2. 주요 사항
	가. Deploy하는 서버 환경 마다 설정 파일을 구분하여 DB 정보와 log가 저장되는 위치를 지정한다.
	나. DB접속 비밀번호는 암호화 하여 환경설정 파일에 저장하고 연결 시 복호화 하여 사용한다.
	다. 의존성 관리는 Maven을 이용한다.

3. 설정
	: 설정정보 로드 순서: Main.java > {서버 타입}_BaseContext.xml > {서버 타입}_properties.xml > {서버 타입}_log4j.xml > ApplicationContext.xml
	
	가. pom.xml
		1) common
			- commons-dbcp : DB 연결 기본 lib
		2) logging
			- log4j
			- log4jdbc-remix : SQL 로그 확장
			- slf4j-api : SQL 로그 확장
			- slf4j-log4j12 : SQL 로그 확장
		3) mysql
			- mysql-connector-java : mysql 기본
		4) DB
			- mybatis
			- mybatis-spring
		5) Transaction explicit (명시적)
			- cglib-nodep : AOP 지원
	나. {서버 타입}_BaseContext.xml
		1) Properties
			- 환경변수 및 DB 접속 정속 정보를 {서버 타입}_properties.xml 형태의 파일로 읽어 들인다.
			- configProp ID로 context 및 프로그램 내에서 사용한다.
		2) Log4j
			- 로그 형식과 레벨등을 정리한 정보를 {서버 타입}_log4j.xml 형태의 파일로 읽어 들인다.
			- File 로그가 저장되는 위치를 file 변수에 지정한다. (현재 /logs/BatchSample/service.out로 되어 있음)
	다. ApplicationContext.xml
		1) Bean scan
			: <context:component-scan base-package="com.corealism.batch" />
			- com.corealism.batch 패키지 하위의 클래스를 읽어서 빈에 등록한다.
			- Main class도 @Component로 선언하여 빈으로 등록된다.
		2) DB
			: 두가지 선언(비밀번호 암호화, SQL 로그 확장)이 포함되어 있다.
			가) 비밀번호 암호화
				- com.corealism.batch.common.util.EncriptBasicDataSource는 org.apache.commons.dbcp.BasicDataSource를 확장한 것으로
					비밀번호를 복호화 하여 설정한다.
				- 만약 암호화 기능을 사용하지 않으려면 class를 org.apache.commons.dbcp.BasicDataSource로 변경한다.
			나) SQL 로그 확장
				- driverClassName을 net.sf.log4jdbc.DriverSpy로 지정하고 DB 접속 URL(properties)이 jdbc:log4jdbc:mysql로 변경하였다.
				- 만약 SQL 로그 확장 기능을 사용하지 않으려면  net.sf.log4jdbc.DriverSpy를 com.mysql.jdbc.Driver로, DB접속 URL을 jdbc:mysql로 변경한다.
				- 로그 확장 기능은 POM의 logging부분 설정의 log4jdbc-remix, slf4j-api, slf4j-log4j12 세가지를 사용한다.
		3) 트렌젝션
			: 명시적 트렌젝션으로 선언되어 사용하고자 하는 메소드에 @Transactional을 선언해야 한다. (선언적 트렌젝션은 CozSample 참고)
			- mapperLocations : SQL이 선언된 Mapper의 위치를 지정
				: Path의 형태로 지정하므로 규칙에 정해서 저장하는 것이 좋다.
				만약 /package/aaa/aaaMapper.xml, /package/bbb/bbbMapper.xml 같은 구조라면 classpath:package/*/*Mapper.xml 형식으로 지정한다.
			- typeAliasesPackage : SQL에 파라메터로 주고 받는 model의 별명을 찾아서 매칭해주는 것으로 model의 위치를 지정한다.
				: Package의 형태로 지정하므로 여러개의 폴더에 나눠진 경우 콤마로 구분하여 지정한다.
			- annotationClass : @Repository로 선언된 Mapper class를 검색하여 빈에 등록한다.
				: Mapper 클래스 검색 시 원치 않는(인터페이스를 사용하는 구조인 경우) class가 검색되는 것을 방지하기 위해 선언
		4) 환경변수
			: 환경변수를 상수로 처리하기 위해 Constants.java의 setConfigProp 메소드를 static하게 만든다.

4. 사용
	가. 환경변수
		: 환경설정 파일의 변수는 환경변수 설정으로 static한 상태이므로 바로 호출해서 사용할 수 있다.
		- Constants.configProp.getProperty("환경설정 파일의 변수명")
		- 환경설정 파일의 변수명은 직접 문자열로 입력해도 되고 변수화 해놔도 상관없다.
			예) private	final static String OUTPUT_DIR = Constants.configProp.getProperty(Constants.SYSTEM_OUTPUTDIR);
	나. 트렌젝션
		: 트렌젝션을 사용할 메소드에 @Transactional을 선언한다.

5. 테스트
	가. 명령 프롬프트 실행
		: java -cp '참조 라이브러리 경로' batchSample.jar '서버 타입'
		- 서버 타입은 local, dev, real 3가지로 설정되어 있음
		- 개발 PC에서 테스트할 경우 라이브러리는 프로젝트 메뉴 Export > Java > Runnable JAR file 에서 Library handling을 'Copy required libraries into a sub-folder next to the generated JAR'를 
		선택하면 현재 '프로젝트명_lib'의 폴더에 필요한 lib가 저장 된다.
		- 로컬에서  batchSample_lib에 참조 lib가 있는 경우 실행 예)
			: java -cp batchSample_lib -jar batchSample.jar local
	나. 셸 스크립트 실행
		: 리눅스와 같은 환경에서 셸을 만들어서 구동하는 경우 jar의 경로를 절대 경로로 잡아준다.
		- 쉘은 생성 후 실행 가능으로 변경한다. (chmod 755 run.sh)
		- run.sh를 만드는 경우 예)
			: >#!/bin/sh
			  >java -cp lib -jar /batchsample/batchsample.jar dev
		- 주의) 셸을 실행하는 위치에 상관없이 파일을 찾도록 절대 경로로 작성한다.

6. 빌드
	가. POM - build
		1) resource
			- src/main/java : SQL이 저장된 xml을 포함시킨다.
			- src/main/resources : 환경 설정과 context 설정 xml을 포함시킨다.
		2) maven-resources-plugin
			: UTF-8 인코딩 설정 때문에 플러그인 설정 사용
		3) maven-compiler-plugin
			: java 버전과 인코딩을 선언하기 위해 사용
		4) maven-jar-plugin
			- finalName : 생성되는 jar 파일명 선언
			- outputDirectory : jar 파일이 생성되는 위치, ${basedir}는 기본변수이며 젠킨스에서 복사하기 편하게 루트로 선언한다.
			- addMavenDescriptor : jar 파일안에 META-INF 폴더에 불필요하게 pom 파일이 포함되는 것을 방지한다.
			- mainClass : jar가 실행될 때 처음 호출되는 메인 클래스를 지정한다.
			- classpathPrefix : MANIFEST.MF 파일안에 클래스패스를 지정한다. 이는 maven-dependency-plugin에서 outputDirectory 경로와 위치상 일치해야 한다.
		5) maven-dependency-plugin
			- outputDirectory : 관련 jar를 위치하게 될 경로
	나. 젠킨스
		1) maven clean
		2) maven package, with pom.xml
		3) Send files or execute commands over SSH
			: Source files를 **/*.jar로 지정하면 lib 폴더도 포함되어 복사된다.

7. 구동
	가. 디플로이 폴더 구조
		루트/
			batchsample.jar (jar안에 환경설정 xml과 context xml이 포함됨)
			run.sh (별도 생성)
			copy.sh (별도 생성)
			---lib/
				{관련lib.jar}
	나. run.sh
		: batchsample.jar 실행
	다. copy.sh
		: 생성된 파일을 원격 서버로 복사
		- 비밀번호를 이용하여 ssh를 이용할 경우 셸 스크립트에 비밀번호를 노출해야 하는 위험이 있어 키를 이용한 자동로그인을 사용함
		1) 키를 이용한 로그인
			가) 개념
				- 접속을 시도하는 서버를 '클라이언트'라하고 접속을 하는 대상 서버를 '리모트서버'라 한다.
				- 클라이언트에서 개인키/공개키 쌍을 생성한다.
				- 클라이언트에서 생성한 공개키를 리모트서버에 등록한다.
				- 클라이언트에서 개인키를 이용하여 리모트서버에 접속한다. 끝~!.  
			나) 키 생성
				: 클라이언트에서 아래 명령으로 키를 생성한다.
					>ssh-keygen -t rsa
					>저장위치 확인? 패스 (기본위치는 홈디랙토리의 .ssh 폴더)
					>비밀번호등록? 패스 (자동로그인을 위해서 생략)
				- 홈디랙토리의 .ssh 폴더에 id_rsa, id_rsa.pub, known_hosts 3개 파일 생성 확인 (authorized_keys 파일은 없을 수 있음)
				- 아래와 같이 각각 권한 설정
					>chmod 700 ~/.ssh
					>chmod 600 ~/.ssh/id_rsa
					>chmod 644 ~/.ssh/id_rsa.pub
					>chmod 644 ~/.ssh/known_hosts
			다) 키 등록
				: 클라이언트에서 공개키를 리모트서버에 복사하고 등록한다.
				- id_rsa.pub 파일을 리모트 서버로 복사한다. (scp 이용)
				- 리모트 서버에서 아래 명령으로 공개키를 인증키 목록에 추가한다.
					>cat id_rsa.pub >> ~/.ssh/authorized_keys
					>chmod 644 ~/.ssh/authorized_keys
			라) 키를 이용한 ssh, scp 사용
				: ssh, scp 이용 시 비밀번호를 입력하지 않아도 접속이 가능하다.
				- 키를 기본위치에 생성하지 않은 경우 ssh, scp의 -i 옵션으로 키 파일의 위치를 지정한다.
			마) authorized_keys에서 키 삭제
				: 키 삭제를 위한 명령은 없음, 삭제할 공개키와 동일한 키를 찾아서 지워야 한다.
		2) 파일 복사
			- 아래 스크립트를 셸로 생성하여 파일을 복사한다.
			>#!/bin/sh
  			>scp -P 1066 /batchsample/test.txt root@222.122.179.145:/tmp
  			- 주의) 셸을 실행하는 위치에 상관없이 파일을 찾도록 절대 경로로 작성한다.
