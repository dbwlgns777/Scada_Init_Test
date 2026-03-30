# DEVICE SERVER

## 0. 이슈(2024-04-09)
TYPE4의 값 중 아래 항목을 제외하고 double이 아닌 long 값이어야함. 혹은 기존 값에 100을 곱한 값이 원래 값.
flag
slide_position_num
slide_position_state
slide_position_mode

따라서 향후 db에 기존 값에 100을 곱하여 다시 저장하거나 long형태로 수정하여 저장해야함.
위 항목 제외하고 TYPE4의 항목들을 아래와 같이 변경필요.
```java
new ZES_Data("slide_position_current",436,"double",0),
```
```java
new ZES_Data("slide_position_current",436,4)
```

## 1. DATA FLOW
SPMS 데이터 수집

-> ict number 기준으로 MySql 실시간 데이터 테이블(pms_real_data_0)에서 현재 값 조회

-> 실시간 데이터 업데이트 

-> 1개의 컬럼이라도 새로운 값이 들어올 경우 로그에 저장

## 2. 사용법
### db config 설정
- 실제 사용되는 property파일은 {project_dir}/resources/에 config.properties, hikari.properties 파일을 넣어놔야 정상실행
- influxDB config
```shell
#influxdb.properties

influxdb.url= //http://123.123.123.123:8086
influxdb.token=
influxdb.org=
influxdb.orgId=
influxdb.bucket=
```
- MySql config
```shell
#hikari.properties

jdbcUrl=
username=
password=
maximumPoolSize= //default 10
maxLifetime= //mysql connection_time_out 시간보다 적게
leakDetectionThreshold=2000 //connection 누수 검사
dataSource.prepStmtCacheSqlLimit=2048 //prepared statment 캐시 사이즈 2048 권장 default 256
dataSource.cachePrepStmts=true // prepared statement cache사용 여부 default false
dataSource.useServerPrepStmts=true // server-side prepared statement 사용 default false

```
### 실행
```shell
./gradlew build
#jar file 생성
#device server로 파일 이동 
nohup java -jar ${생성된 jar file} ${PORT번호(기본값 9600)} &
```


### 디바이스 셋팅 쉘 스크립트
- configure_influxdb_device_server.sh 실행
- 서버로 옮긴 후 실행 권한추가, 실행
```shell
chmod +x configure_influxdb_device_server.sh
./configure_influxdb_device_server.sh
```
####실행 후 
1. /home/zestech 에 influxdb_device_server 디렉토리 생성
2. /usr/local/bin에 start.sh 생성
- 서버 자동 시작 프로그램. 서버 올릴 포트번호 넣어주면 자동으로 그 포트로 실행. 기본값 9600
```shell
start 포트번호
```
3. /etc/logrotate.d/nohup 파일 생성
- nohup.out 1주일 단위로 자동 로테이트

