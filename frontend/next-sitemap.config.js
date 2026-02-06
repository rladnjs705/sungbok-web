/** @type {import('next-sitemap').IConfig} */
module.exports = {
  siteUrl: process.env.SITE_URL || 'https://sungbok-church.com',
  generateRobotsTxt: true,
  generateIndexSitemap: false,
  exclude: ['/login', '/register', '/api/*'],
  robotsTxtOptions: {
    policies: [
      {
        userAgent: '*',
        allow: '/',
        disallow: ['/login', '/register', '/api'],
      },
    ],
  },
  transform: async (config, path) => {
    // 특정 경로에 대한 우선순위 설정
    let priority = 0.7;
    let changefreq = 'weekly';

    if (path === '/') {
      priority = 1.0;
      changefreq = 'daily';
    } else if (path.startsWith('/worship')) {
      priority = 0.9;
      changefreq = 'weekly';
    } else if (path.startsWith('/sermons')) {
      priority = 0.8;
      changefreq = 'weekly';
    } else if (path.startsWith('/news')) {
      priority = 0.7;
      changefreq = 'daily';
    }

    return {
      loc: path,
      changefreq,
      priority,
      lastmod: config.autoLastmod ? new Date().toISOString() : undefined,
    };
  },
};
