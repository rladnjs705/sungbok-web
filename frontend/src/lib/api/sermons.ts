// Mock API for sermons data
// TODO: Replace with actual API endpoint when backend is ready

export interface Sermon {
  id: number;
  title: string;
  date: string;
  category: '주일예배' | '금요생수의강' | '월삭예배' | '특별집회' | '수요예배' | '새벽예배' | '주일5부';
  videoId: string;
  preacher: string;
  verse: string;
  thumbnail: string;
  views?: number;
  duration?: string;
}

// Simulate API delay
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

export async function getSermons(): Promise<Sermon[]> {
  await delay(300); // Simulate network delay

  return [
    {
      id: 1,
      title: '하나님의 은혜로 살아가는 삶',
      date: '2024.03.17',
      category: '주일예배',
      videoId: 'NpjUJd1EoJI',
      preacher: '김성복 목사',
      verse: '에베소서 2:8-9',
      thumbnail: 'https://img.youtube.com/vi/NpjUJd1EoJI/hqdefault.jpg',
      views: 1234,
      duration: '45:32',
    },
    {
      id: 2,
      title: '믿음으로 승리하는 삶',
      date: '2024.03.15',
      category: '금요생수의강',
      videoId: 'dQw4w9WgXcQ',
      preacher: '김성복 목사',
      verse: '요한일서 5:4-5',
      thumbnail: 'https://img.youtube.com/vi/dQw4w9WgXcQ/hqdefault.jpg',
      views: 892,
      duration: '38:15',
    },
    {
      id: 3,
      title: '감사의 제사를 드리는 자',
      date: '2024.03.13',
      category: '수요예배',
      videoId: '9bZkp7q19f0',
      preacher: '김성복 목사',
      verse: '시편 50:23',
      thumbnail: 'https://img.youtube.com/vi/9bZkp7q19f0/hqdefault.jpg',
      views: 567,
      duration: '32:45',
    },
    {
      id: 4,
      title: '십자가의 능력',
      date: '2024.03.10',
      category: '주일예배',
      videoId: 'kJQP7kiw5Fk',
      preacher: '김성복 목사',
      verse: '고린도전서 1:18',
      thumbnail: 'https://img.youtube.com/vi/kJQP7kiw5Fk/hqdefault.jpg',
      views: 1456,
      duration: '42:20',
    },
    {
      id: 5,
      title: '주님의 사랑',
      date: '2024.03.08',
      category: '금요생수의강',
      videoId: 'YQHsXMglC9A',
      preacher: '김성복 목사',
      verse: '요한복음 3:16',
      thumbnail: 'https://img.youtube.com/vi/YQHsXMglC9A/hqdefault.jpg',
      views: 743,
      duration: '35:10',
    },
    {
      id: 6,
      title: '월삭 감사 예배',
      date: '2024.03.01',
      category: '월삭예배',
      videoId: 'jNQXAC9IVRw',
      preacher: '김성복 목사',
      verse: '시편 100:4-5',
      thumbnail: 'https://img.youtube.com/vi/jNQXAC9IVRw/hqdefault.jpg',
      views: 432,
      duration: '40:05',
    },
    {
      id: 7,
      title: '부활의 소망',
      date: '2024.02.28',
      category: '특별집회',
      videoId: 'L_jWHffIx5E',
      preacher: '김성복 목사',
      verse: '고린도전서 15:20-22',
      thumbnail: 'https://img.youtube.com/vi/L_jWHffIx5E/hqdefault.jpg',
      views: 2134,
      duration: '55:30',
    },
    {
      id: 8,
      title: '새벽을 깨우는 기도',
      date: '2024.02.26',
      category: '새벽예배',
      videoId: 'fJ9rUzIMcZQ',
      preacher: '김성복 목사',
      verse: '시편 5:3',
      thumbnail: 'https://img.youtube.com/vi/fJ9rUzIMcZQ/hqdefault.jpg',
      views: 234,
      duration: '25:15',
    },
    {
      id: 9,
      title: '영어예배 - Grace and Peace',
      date: '2024.02.25',
      category: '주일5부',
      videoId: 'Zi_XLOBDo_Y',
      preacher: 'Rev. Joo-Chan Choi',
      verse: 'Ephesians 2:8-9',
      thumbnail: 'https://img.youtube.com/vi/Zi_XLOBDo_Y/hqdefault.jpg',
      views: 567,
      duration: '42:00',
    },
    {
      id: 10,
      title: '성령의 열매',
      date: '2024.02.21',
      category: '수요예배',
      videoId: '60ItHLz5WEA',
      preacher: '김성복 목사',
      verse: '갈라디아서 5:22-23',
      thumbnail: 'https://img.youtube.com/vi/60ItHLz5WEA/hqdefault.jpg',
      views: 678,
      duration: '33:40',
    },
    {
      id: 11,
      title: '하나님의 부르심',
      date: '2024.02.18',
      category: '주일예배',
      videoId: 'kXYiU_JCYtU',
      preacher: '김성복 목사',
      verse: '로마서 8:28-30',
      thumbnail: 'https://img.youtube.com/vi/kXYiU_JCYtU/hqdefault.jpg',
      views: 1345,
      duration: '46:50',
    },
    {
      id: 12,
      title: '주의 인자하심',
      date: '2024.02.16',
      category: '금요생수의강',
      videoId: 'hT_nvWreIhg',
      preacher: '김성복 목사',
      verse: '시편 103:8-14',
      thumbnail: 'https://img.youtube.com/vi/hT_nvWreIhg/hqdefault.jpg',
      views: 456,
      duration: '37:25',
    },
  ];
}

export async function getSermonById(id: number): Promise<Sermon | null> {
  await delay(200);
  const sermons = await getSermons();
  return sermons.find(s => s.id === id) || null;
}

export async function getSermonsByCategory(category: string): Promise<Sermon[]> {
  await delay(200);
  const sermons = await getSermons();

  if (category === '전체') {
    return sermons;
  }

  return sermons.filter(s => s.category === category);
}

export async function getFeaturedSermon(): Promise<Sermon | null> {
  await delay(150);
  const sermons = await getSermons();
  return sermons[0] || null;
}
