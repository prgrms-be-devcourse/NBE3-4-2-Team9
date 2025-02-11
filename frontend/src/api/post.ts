import { ApiResponse } from '@/types/common/ApiResponse';
import { PageResponse } from '@/types/common/PageResponse';
import { PostResponse } from '@/types/post/PostResponse';
import publicApi from './axios';
import privateApi from './axios';

interface GetPostsParams {
  categoryId?: number;
  keyword?: string;
  sort?: 'latest' | 'popular';
  page?: number;
  size?: number;
}

export const getPosts = async (params: GetPostsParams = {}): Promise<ApiResponse<PageResponse<PostResponse>>> => {
  const response = await publicApi.get('/api/v1/posts', { params });
  return response.data;
};

export const getPost = async (id: number): Promise<ApiResponse<PostResponse>> => {
  const response = await publicApi.get(`/api/v1/posts/${id}`);
  return response.data;
};

interface CreatePostRequest {
  subject: string;
  content: string;
  categoryId: number;
}

export const createPost = async (postData: {
  subject: string;
  content: string;
  categoryId: string;
  authorId: string;
  authorName: string;
  authorImg?: string;
}) => {
  const response = await privateApi.post('/api/v1/posts', postData);
  return response.data;
}; 