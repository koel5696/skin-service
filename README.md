<h1 align="center">Layer'd</h1>
<p align="center">
  피부 상태 기반 성분 · 제품 · 루틴 추천 시스템
</p>

## 1. Overview

- Layer'd 는 사용자의 피부 상태를 정량화하여 성분 → 제품 → 루틴까지 연결하는 추천 시스템입니다.
- 기존의 단순 피부 타입 기반 추천을 넘어 상태 벡터와 성분군 매칭을 기반으로 개인화된 결과를 제공합니다.

## 2. Service Introduction
<img width="8000" height="4500" alt="SWYP 3팀 최종 발표_page-0001" src="https://github.com/user-attachments/assets/349f59ce-7236-4760-a08a-88764aff9be5" />
<img width="8000" height="4500" alt="SWYP 3팀 최종 발표_page-0002" src="https://github.com/user-attachments/assets/9a1ffe26-9c1e-48e9-8071-d437598a9f3e" />
<img width="8000" height="4500" alt="SWYP 3팀 최종 발표_page-0003" src="https://github.com/user-attachments/assets/c1067a8f-d7d2-4df8-85cd-97b5e21e4a24" />
<img width="8000" height="4500" alt="SWYP 3팀 최종 발표_page-0004" src="https://github.com/user-attachments/assets/5495c119-8fcd-418b-a08f-b74ee07fa760" />
<img width="8000" height="4500" alt="SWYP 3팀 최종 발표_page-0005" src="https://github.com/user-attachments/assets/816394bb-debd-4a94-ad80-ec5de65c429f" />
<img width="8000" height="4500" alt="SWYP 3팀 최종 발표_page-0006" src="https://github.com/user-attachments/assets/19106e47-d149-42a9-9877-1dac505ba1db" />
<img width="8000" height="4500" alt="SWYP 3팀 최종 발표_page-0007" src="https://github.com/user-attachments/assets/94c45b47-efcf-4a5c-81e2-83cad8b75c8e" />
<img width="8000" height="4500" alt="SWYP 3팀 최종 발표_page-0008" src="https://github.com/user-attachments/assets/65530410-7349-4ddd-b66b-3a3f728f496e" />
<img width="8000" height="4500" alt="SWYP 3팀 최종 발표_page-0009" src="https://github.com/user-attachments/assets/02f0d6ca-3a5b-47de-bcac-08201d4399ac" />
<img width="8000" height="4500" alt="SWYP 3팀 최종 발표_page-0010" src="https://github.com/user-attachments/assets/21c825b0-c86c-4e0a-8043-b143df5d999a" />
<img width="8000" height="4500" alt="SWYP 3팀 최종 발표_page-0011" src="https://github.com/user-attachments/assets/871ddeea-f2ad-4225-974b-b9ecf562b144" />
<img width="8000" height="4500" alt="SWYP 3팀 최종 발표_page-0012" src="https://github.com/user-attachments/assets/05bd4bce-efee-47f3-ae82-1752e1987ba3" />
<img width="8000" height="4500" alt="SWYP 3팀 최종 발표_page-0013" src="https://github.com/user-attachments/assets/93b43ee7-9228-42b9-9ef5-3a9e65bd17d5" />
<img width="8000" height="4500" alt="SWYP 3팀 최종 발표_page-0014" src="https://github.com/user-attachments/assets/52e8b7c0-187a-4e9b-aa9c-4e018d3668b0" />
<img width="8000" height="4500" alt="SWYP 3팀 최종 발표_page-0015" src="https://github.com/user-attachments/assets/f59e1fa4-519e-4c73-b579-2393bda99565" />
<img width="8000" height="4500" alt="SWYP 3팀 최종 발표_page-0016" src="https://github.com/user-attachments/assets/b90659fb-9582-49d5-9f7e-fc2a540adc31" />
<img width="8000" height="4500" alt="SWYP 3팀 최종 발표_page-0017" src="https://github.com/user-attachments/assets/b80e3bba-fd1c-46d5-bc6e-098cecd37150" />

## 2. Problem & Solution

### Problem

| 문제 | 설명 |
|------|------|
| 피부 타입 중심 추천 | 실제 피부 상태 반영 부족 |
| 성분 기반 기준 부족 | 추천 근거 불명확 |
| 개인화 한계 | 사용자별 차이 반영 어려움 |

### Solution

| 해결 방식 | 설명 |
|----------|------|
| 상태 벡터화 | 피부 상태를 수치로 변환 |
| 성분군 점수화 | 필요 성분 우선순위 도출 |
| 매칭 기반 추천 | 제품 성분 구성과 비교 |

---

## 3. Features

| 기능 | 설명 |
|------|------|
| 인증 | OAuth2 + JWT 기반 로그인 |
| 피부 진단 | 설문 기반 상태 분석 |
| 성분 추천 | Top3 성분군 및 추천 이유 제공 |
| 제품 추천 | Need-Supply 기반 점수 계산 |
| 루틴 생성 | 피부 상태 기반 스킨케어 루틴 |

---

## 4. Tech Stack

### Backend

| 기술 | 설명 |
|------|------|
| Java 17 | 언어 |
| Spring Boot | 서버 프레임워크 |
| Spring Security | 인증/인가 |
| JPA (Hibernate) | ORM |

### Infrastructure

| 기술 | 설명 |
|------|------|
| AWS EC2 | 애플리케이션 서버 |
| AWS RDS (MySQL) | 데이터베이스 |

### Cache

| 기술 | 설명 |
|------|------|
| Caffeine | 로컬 캐시 |
