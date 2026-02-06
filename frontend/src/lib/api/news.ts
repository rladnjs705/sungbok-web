// Mock API for news data
// TODO: Replace with actual API endpoint when backend is ready

export interface NewsItem {
  id: number;
  title: string;
  date: string;
  category: '공지사항' | '주보' | '새소식' | '행사';
  excerpt: string;
  content: string;
  image?: string;
  author: string;
  views?: number;
  featured?: boolean;
}

// Simulate API delay
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

export async function getNews(): Promise<NewsItem[]> {
  await delay(300);

  return [
    {
      id: 1,
      title: '부활절 연합예배 안내',
      date: '2024.03.15',
      category: '공지사항',
      excerpt: '부활절을 맞아 모든 부서가 함께하는 연합예배를 드립니다.',
      content: '부활절 연합예배가 3월 31일(일) 오전 10시에 진행됩니다. 모든 부서가 함께 모여 주님의 부활을 축하하고 경배하는 시간을 갖겠습니다.',
      image: '/images/news01.jpg',
      author: '담임목사',
      views: 1234,
      featured: true,
    },
    {
      id: 2,
      title: '청년부 수련회',
      date: '2024.03.10',
      category: '행사',
      excerpt: '3월 22-24일, 청년부 겨울 수련회가 진행됩니다.',
      content: '청년부 수련회가 3월 22일부터 24일까지 2박 3일 일정으로 진행됩니다. 장소는 경기도 가평 소재 수양관입니다.',
      image: '/images/news02.jpg',
      author: '청년부',
      views: 892,
      featured: true,
    },
    {
      id: 3,
      title: '새가족 환영',
      date: '2024.03.05',
      category: '새소식',
      excerpt: '3월 첫째 주 새가족을 진심으로 환영합니다.',
      content: '3월 첫째 주에 등록하신 새가족 여러분을 진심으로 환영합니다. 새가족 교육은 매주 주일 오후 2시에 진행됩니다.',
      image: '/images/news03.jpg',
      author: '새가족부',
      views: 567,
      featured: true,
    },
    {
      id: 4,
      title: '3월 셋째 주 주보',
      date: '2024.03.17',
      category: '주보',
      excerpt: '3월 셋째 주 주일 예배 순서 및 교회 소식',
      content: '예배순서: 찬양 - 기도 - 말씀 - 헌금 - 축도',
      image: '/images/weekly_bulletin.jpg',
      author: '사무국',
      views: 2341,
    },
    {
      id: 5,
      title: '선교 후원의 밤',
      date: '2024.03.08',
      category: '행사',
      excerpt: '선교 사역을 위한 후원의 밤 행사가 열립니다.',
      content: '선교 후원의 밤이 3월 20일 저녁 7시에 본당에서 열립니다. 선교 현장의 소식과 간증을 나누는 시간이 준비되어 있습니다.',
      author: '선교부',
      views: 445,
    },
    {
      id: 6,
      title: '어린이 부활절 행사',
      date: '2024.03.12',
      category: '행사',
      excerpt: '어린이들을 위한 부활절 특별 프로그램',
      content: '어린이들을 위한 부활절 달걀 찾기와 공연이 준비되어 있습니다.',
      image: '/images/easter_kids.jpg',
      author: '어린이부',
      views: 678,
    },
    {
      id: 7,
      title: '3월 둘째 주 주보',
      date: '2024.03.10',
      category: '주보',
      excerpt: '3월 둘째 주 주일 예배 순서',
      content: '이번 주 말씀: 요한복음 3:16',
      author: '사무국',
      views: 1890,
    },
    {
      id: 8,
      title: '장년부 성경공부',
      date: '2024.03.06',
      category: '공지사항',
      excerpt: '장년부 성경공부 시간 변경 안내',
      content: '장년부 성경공부 시간이 매주 수요일 오후 7시 30분으로 변경됩니다.',
      author: '장년부',
      views: 523,
    },
    {
      id: 9,
      title: '찬양팀 신입 모집',
      date: '2024.03.03',
      category: '공지사항',
      excerpt: '찬양팀에서 신입 멤버를 모집합니다.',
      content: '찬양팀 신입 멤버를 모집합니다. 관심 있으신 분은 예배부로 연락주세요.',
      author: '예배부',
      views: 789,
    },
    {
      id: 10,
      title: '3월 첫째 주 주보',
      date: '2024.03.03',
      category: '주보',
      excerpt: '3월 첫째 주 주일 예배 순서',
      content: '이번 주 말씀: 시편 23편',
      author: '사무국',
      views: 1567,
    },
  ];
}

export async function getNewsById(id: number): Promise<NewsItem | null> {
  await delay(200);
  const news = await getNews();
  return news.find(n => n.id === id) || null;
}

export async function getNewsByCategory(category: string): Promise<NewsItem[]> {
  await delay(200);
  const news = await getNews();

  if (category === '전체') {
    return news;
  }

  return news.filter(n => n.category === category);
}

export async function getFeaturedNews(): Promise<NewsItem[]> {
  await delay(150);
  const news = await getNews();
  return news.filter(n => n.featured).slice(0, 3);
}

export async function searchNews(query: string): Promise<NewsItem[]> {
  await delay(300);
  const news = await getNews();
  const lowerQuery = query.toLowerCase();

  return news.filter(
    n =>
      n.title.toLowerCase().includes(lowerQuery) ||
      n.content.toLowerCase().includes(lowerQuery) ||
      n.excerpt.toLowerCase().includes(lowerQuery)
  );
}
