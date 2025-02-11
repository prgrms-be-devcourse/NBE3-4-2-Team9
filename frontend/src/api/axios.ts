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

// 관리자 전용 엔드포인트 목록
const adminEndpoints = [
  '/api/v1/category',
  '/api/v1/adm'
];

// 인증이 필요한 요청에만 토큰 추가
privateApi.interceptors.request.use(
  (config) => {
    const isAdmin = localStorage.getItem('isAdmin') === 'true';
    const adminToken = localStorage.getItem('adminToken');
    const userToken = localStorage.getItem('accessToken') || localStorage.getItem('token');

    // 관리자 전용 엔드포인트 체크
    const isAdminEndpoint = adminEndpoints.some(endpoint => 
      config.url?.includes(endpoint)
    );

    // 관리자 전용 엔드포인트거나 관리자로 로그인한 경우 adminToken 사용
    if ((isAdminEndpoint || isAdmin) && adminToken) {
      config.headers['Authorization'] = `Bearer ${adminToken}`;
    } 
    // 그 외의 경우 userToken 사용
    else if (userToken) {
      config.headers['Authorization'] = `Bearer ${userToken}`;
    }
    
    console.log('Request URL:', config.url);
    console.log('Is admin endpoint:', isAdminEndpoint);
    console.log('Is admin:', isAdmin);
    console.log('Token used:', config.headers['Authorization']);
    
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