# 스프링 DB 1편 - 데이터 접근 핵심 원리
reference : [스프링 DB 1편 - 데이터 접근 핵심 원리](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-db-1)
* 하게 된 이유 : 로드맵 과정 빠르게 익히면서 개념 이해하기 + 정리
* 프로젝트 시작일 : 12/23
* 프로젝트 종료일 : 

## 개발환경
* gradle
* java 17
* spring boot 3.4.1
* library
    * lombok
    * h2
    * jdbc

## Things I am doing
DB 관련 용어
* `트랜잭션` : 데이터 변경 관련 작업들이 하나의 작업처럼 처리되며, 성공과 실패가 분명하고 상호 독립적인 시스템 혹은 개념([참고](https://ko.wikipedia.org/wiki/%EB%8D%B0%EC%9D%B4%ED%84%B0%EB%B2%A0%EC%9D%B4%EC%8A%A4_%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98))
* `커밋` : 트랜잭션 작업 내 데이터 변경이 DB 정상 반영됨 (=데이터 처리 성공)
* `롤백` : 트랜잭션 작업 내 데이터 변경이 DB 정상 반영 안됐음 (=데이터 처리 실패)

## Why
* 데이터를 파일에 저장하지 않고, 데이터베이스를 사용해서 저장하는 이유 : 트랜잭션 개념(ACID)을 지원함
