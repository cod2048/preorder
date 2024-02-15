# 예약구매 서비스

## 목차
[1. 기능 소개](#기능-소개)<br/>
[2. 프로젝트 설명](#프로젝트-설명)<br/>
[3. 프로젝트 설치&실행방법](#프로젝트-설치--실행-방법)(미완성)<br/>
[4. 성능 테스트](#성능-테스트)(미완성)<br/>
[5. 문제 및 해결](#문제-및-해결)(미완성)

## 기능 소개

- **유저 관리**
    - [x] 이메일 인증을 통한 회원가입
    - [x] jwt 토큰을 이용한 로그인
    - [x] 유저는 판매자 / 구매자로 구분
- **상품 관리**
    - [x] 판매자는 상품을 등록할 수 있음
    - [x] 구매자는 상품목록 / 상세조회를 통해 상품을 확인 가능
    - [x] 상품은 일반상품 / 예약구매 상품으로 구분
- **구매**
    - [x] 상품 상세 페이지에서 구매요청을 보냄
    - [x] 예약구매의 경우 특정 시간에 주문이 몰릴 것을 가정
    - [ ] TODO: 4주차 진행 후 내용 추가
- **주문 정보**
    - [x] 주문한 상품 상세정보 확인


## 프로젝트 설명

### 개발 환경
- 사용언어 : `Java`
- 프레임워크 : `Spring`
- 데이터베이스 : `Mysql`, `redis`
- ORM : `JPA`
- IDE : `IntelliJ`

### ERD
![ERD image](./erd.png)


### API 명세서

| 기능        | Method | URL                       | Request                                           | Response                                                                 |
|-------------|--------|---------------------------|---------------------------------------------------|--------------------------------------------------------------------------|
| 회원가입    | POST   | /api/users/signup         | { 'name', 'email', 'password', 'role' }           | { header: 201, data: { 'email': '사용된 이메일' } }                      |
| 로그인      | POST   | /api/users/login          | { 'email', 'password' }                           | { header: 200, data: { 'email': '로그인 된 사용자 이메일' } }            |
| 상품 등록   | POST   | /api/products             | { 'user_num', 'title', 'description', 'price' }   | { header: 201, data: { 'title': '생성된 상품명' } }                      |
| 상품 목록 조회 | GET    | /api/products             | -                                                 | { header: 200, data: { '[상품 리스트]' } }                               |
| 상품 상세 페이지 | GET    | /api/products/{productNum} | -                                                  | { header: 200, data: { 'title', 'description', 'price', 'stock' } }      |
| 주문 생성   | POST   | /api/orders               | { 'order_num', 'user_num', 'quantity', 'address' } | { header: 201, data: { } }                                            |
| 결제 시도   | POST   | /api/payments             | { 'order_num', 'user_num' }                        | { header: 201, data: { } }                                              |
| 주문 정보   | GET    | /api/orders/{orderID}     | -                                                  | { header: 200, data: { 'user_num', 'product_num', 'quantity', 'address', 'status', 'order_time' } } |

