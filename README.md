# 서비스 소개

## 1. 서비스 설명

**개요**

- 한줄소개 : 아이들이 스마트폰을 이용하여 친구들과 밖에서 뛰어놀 수 있는 액티비티 컨텐츠를 제공하는 App
- 서비스명 : **나와, 놀자!**

**타켓**

- 스마트폰을 이용하여 친구들과 밖에서 뛰어놀고 싶은 아이들
- 정적인 게임 어플이 지루한 아이들

## 2. 기획 배경

**배경**

코로나 팬데믹 이후, 아이들의 스마트폰 의존도가 갈수록 높아지고 있습니다. 이로 인해 아이들의 활동량이 현저히 줄어들어 체력 저하, 소아비만 비율이 늘어나는 등의 문제가 발생하고 있습니다.

**목적**

아이들이 스마트폰을 이용하여 밖에서 친구들과 활동적인 게임을 할 수 있도록 다양한 액티비티 게임 컨텐츠를 제공함으로써 아이들의 야외 활동을 장려하기 위해 프로젝트를 기획하였습니다.

**설명**

- 기존 숨바꼭질 놀이에 GPS 기반 게임적 요소를 추가하여 스마트폰이 익숙한 아이들의 흥미를 유발시키면서 미션을 통한 색다른 즐거움 제공합니다.
- 실시간 위치를 기반으로 현재 내 주변의 귀여운 캐릭터를 수집하며 다양한 미니게임을 제공합니다.
- 추가적으로 휴대성이 좋은 갤럭시 워치를 연동하여 보다 게임을 편하게 진행할 수 있도록 하였습니다.
- '나와, 놀자!' 어플을 통해 아이들이 밖에서 활동적으로 움직이며 활동량을 늘림과 동시에 아이스 브레이킹, 사회성 향상의 효과를 기대하고 있습니다.

## 3. 서비스 화면

1. 로그인 & 메인화면

![로그인메인화면](https://user-images.githubusercontent.com/37400699/203257549-ea69f1ba-bd36-4a94-8a92-7d5f86bf4c58.png)

2. 숨바꼭질 

![게임_설정](https://user-images.githubusercontent.com/37400699/203257600-e55de524-b4c3-4a96-9ded-1d819ec84c81.jpg)
![입장](https://user-images.githubusercontent.com/37400699/203257772-dee8f240-6f74-45ed-bdc1-7672e1df463b.jpg)

![술래](https://user-images.githubusercontent.com/37400699/203257814-a3140ce1-a590-46c3-a787-abbfefc8315b.jpg)
![숨는팀_화면](https://user-images.githubusercontent.com/37400699/203258127-8ac8ab27-593e-492a-8462-80ad084a9ba2.jpg)

![결과](https://user-images.githubusercontent.com/37400699/203258220-03fc8c37-e363-4578-a1e7-8d8655004d63.jpg)
![워치](https://user-images.githubusercontent.com/37400699/203258273-f071fb13-1235-4092-a95c-777408660962.png)

3. 캐릭터 수집

![캐릭터_도감](https://user-images.githubusercontent.com/37400699/203258497-540d5aa3-bdd4-4d3c-a095-ea1d269d49e2.jpg)
![캐릭터수집](https://user-images.githubusercontent.com/37400699/203258662-3d0892b2-ea24-4878-9a81-9e78e9cae36e.jpg)

![게임1](https://user-images.githubusercontent.com/37400699/203258807-4cbaf118-239c-421c-9c61-c9b0ce1dbc40.jpg)
![게임2](https://user-images.githubusercontent.com/37400699/203258815-2f24a247-16f3-43e9-a4a9-85164c9ede1f.jpg)

![게임3](https://user-images.githubusercontent.com/37400699/203258824-93670e94-7c8c-4905-9cd8-baa73beeffa3.jpg)
![게임4](https://user-images.githubusercontent.com/37400699/203258833-b4647cf0-6457-49af-9599-8873548bbe81.jpg)

# 프로젝트 아키택쳐

## 1.  아키텍쳐 - ERD

![erd](https://user-images.githubusercontent.com/37400699/203259059-e0690819-888c-428e-9cda-c1e21a2af6cf.png)

## 2. 아키텍쳐 - PROJECT 

![아키텍처](https://user-images.githubusercontent.com/37400699/203259068-aa1a7037-506d-4a65-b047-9796dc8243fb.png)

## 3. 웹 소켓 (STOMP)

![웹소켓](https://user-images.githubusercontent.com/37400699/203259077-61cdee35-6169-4704-bb2d-19c76569f418.png)

## 4. 기술 스택

![기술스택](https://user-images.githubusercontent.com/37400699/203259083-3fd34ff3-4cef-4a85-b56d-f8c5a7abaa2f.png)

# 프로젝트 진행

Agile 방법으로 프로젝트를 진행했으며 Jira를 사용하여 프로젝트 일정 및 이슈를 관리하였습니다. 또한, Scrum 방식을 채택하여 일주일 단위의 스프린트를 통해 커뮤니케이션 리소스를 줄였습니다.

## 1. Git Flow

Git Flow는 최종 배포를 위한 Master, 배포 및 테스트를 위한 develop, 세부 기능을 개발할 수 잇는 feature/(기능명) 브랜치로 구성하여 Git Flow를 구성하였습니다.

![깃플로우](https://user-images.githubusercontent.com/37400699/203259624-939fe49f-5dff-41a0-ae3f-fa0b683afca2.png)

## 2. Jira

매주 월요일에 스프린트 시작, 데일리 스크럼을 통해 팀원들의 진행상황 공유하였습니다.

- 에픽은 가장 큰 단위인 공통일정, 기획, 설계, 홈 화면, 숨바꼭질, 캐릭터 수집, CI/CD 등으로 구성하였습니다.
- 스토리는 해야 할 기능을 적었으며 그 밑에 sub task를 두어 세부적으로 할 일을 적었습니다.

![번다운차트](https://user-images.githubusercontent.com/37400699/203259629-8816d34c-7100-49cf-baff-ddfc1cb32da0.png)

## 3. Notion

데일리 스크럼 , API문서, 발표정리 등 문서들에 대한 자료는 Notion을 통해서 작성했습니다.

![노션1](https://user-images.githubusercontent.com/37400699/203259638-b4d60a59-84bd-42d8-95ce-c2abec82215c.png)

![노션2](https://user-images.githubusercontent.com/37400699/203259648-c6f9eb45-f580-487e-be98-44821a470dd1.png)

![노션3](https://user-images.githubusercontent.com/37400699/203259651-4556137a-7e86-4332-ab3a-796387652d9a.png)

# 배포

[포팅메뉴얼](https://lab.ssafy.com/s07-final/S07P31D103/-/blob/develop/exec/%EC%9E%90%EC%9C%A8PJT_%EA%B5%AC%EB%AF%B81%EB%B0%98_D103_%ED%8F%AC%ED%8C%85%EB%A7%A4%EB%89%B4%EC%96%BC.pdf) 을 참고하세요.

# 팀원 소개

![팀원소개1](https://user-images.githubusercontent.com/37400699/203259811-82af1129-86ff-4e23-9a42-4ce9acc15b2f.png)

![팀원소개2](https://user-images.githubusercontent.com/37400699/203259817-98fb66c5-8ae2-4a18-829a-7962ad9630f7.png)
