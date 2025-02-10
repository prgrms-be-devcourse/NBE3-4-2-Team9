import { ApiResponse } from '@/types/common/ApiResponse';
import { JobPostingSearchCondition } from '@/types/job-posting/JobPostingSearchCondition';
import { PageResponse } from '@/types/common/PageResponse';
import { JobPostingPageResponse } from '@/types/job-posting/JobPostingPageResponse';
import { privateApi } from './axios';

export const getJobPostings = async (params: JobPostingSearchCondition): Promise<ApiResponse<PageResponse<JobPostingPageResponse>>> => {
  const response = await privateApi.get('/api/v1/job-postings', { params });
  return response.data;
};

export const getJobPosting = async (id: number): Promise<ApiResponse<JobPostingPageResponse>> => {
  const response = await privateApi.get(`/api/v1/job-postings/${id}`);
  return response.data;
};

export const getJobPostingDetail = async (id: number) => {
  const response = await privateApi.get(`/api/v1/job-postings/${id}`);
  return response.data;
}; 