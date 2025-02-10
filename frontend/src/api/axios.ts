import axios from 'axios';

const BASE_URL = 'http://localhost:8080';

// 인증이 필요한 요청을 위한 인스턴스
export const privateApi = axios.create({
  baseURL: BASE_URL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 인증이 필요없는 요청을 위한 인스턴스
export const publicApi = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  withCredentials: true
});

// 인증이 필요한 요청에만 토큰 추가
privateApi.interceptors.request.use(
  (config) => {
    // 카테고리 관리 엔드포인트는 adminToken 사용
    if (config.url?.includes('/api/v1/category') &&
        (config.method === 'post' || config.method === 'delete' || config.method === 'patch')) {
      const adminToken = localStorage.getItem('adminToken');
      if (adminToken) {
        config.headers['Authorization'] = `Bearer ${adminToken}`;
      }
    } else {
      // 관리자로 로그인한 경우 adminToken 사용, 아닌 경우 일반 토큰 사용
      const isAdmin = localStorage.getItem('isAdmin') === 'true';
      const token = isAdmin 
        ? localStorage.getItem('adminToken')
        : (localStorage.getItem('accessToken') || localStorage.getItem('token'));
        
      if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
      }
    }
    console.log('Request headers:', config.headers);
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 에러 발생 시 상세 정보 로깅
privateApi.interceptors.response.use(
  (response) => response,
  async (error) => {
    console.error('API Error:', {
      endpoint: error.config?.url,
      method: error.config?.method,
      requestHeaders: error.config?.headers,
      status: error.response?.status,
      errorCode: error.response?.data?.code,
      message: error.response?.data?.message
    });
    return Promise.reject(error);
  }
);

export default privateApi;