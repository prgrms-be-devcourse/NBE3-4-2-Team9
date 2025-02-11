'use client';

import { useEffect, useState } from 'react';
import { PostResponse } from '@/types/post/PostResponse';
import { JobPostingPageResponse } from '@/types/job-posting/JobPostingPageResponse';
import { getPosts } from '@/api/post';
import { getJobPostings } from '@/api/jobPosting';
import Link from 'next/link';
import { formatDate } from '@/utils/dateUtils';
import { ChatBubbleLeftIcon, ArrowRightIcon, BriefcaseIcon, MagnifyingGlassIcon } from '@heroicons/react/24/outline';
import JobPostingCard from '@/components/job/JobPostingCard';
import { Category } from '@/types/post/Category';
import { getCategories } from '@/api/category';
import { JobPosting } from '@/types/jobposting';
import { privateApi } from '@/api/axios';

export default function Home() {
  const [posts, setPosts] = useState<PostResponse[]>([]);
  const [jobPostings, setJobPostings] = useState<JobPosting[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [categories, setCategories] = useState<Category[]>([]);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await getCategories();
        if (response.success) {
          setCategories(response.data);
        }
      } catch (error) {
        console.error('Failed to fetch categories:', error);
      }
    };

    fetchCategories();
  }, []);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [postsResponse, jobPostingsResponse] = await Promise.all([
          getPosts({ 
            page: 0, 
            size: 5,
            sort: 'latest'
          }),
          privateApi.get('/api/v1/job-posting', {
            params: {
              pageNum: 0,
              pageSize: 3
            }
          })
        ]);

        if (postsResponse.success) {
          setPosts(postsResponse.data.content);
        }

        if (jobPostingsResponse.data.success) {
          setJobPostings(jobPostingsResponse.data.data.content);
        }
      } catch (error) {
        console.error('Failed to fetch data:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, []);

  return (
    <main className="min-h-screen bg-gray-50">
      {/* 히어로 섹션 */}
      <div className="bg-gradient-to-r from-blue-600 to-blue-800 text-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
          <div className="text-center">
            <h1 className="text-4xl font-bold mb-4">
              IT 채용 정보를 한눈에
            </h1>
            <p className="text-xl text-blue-100 mb-8">
              최신 개발자 채용 공고를 찾아보세요
            </p>
            
            {/* 검색바 */}
            <div className="max-w-2xl mx-auto">
              <div className="relative">
                <MagnifyingGlassIcon className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                <input
                  type="text"
                  placeholder="기술 스택, 회사명으로 검색"
                  className="w-full pl-10 pr-4 py-3 rounded-lg text-gray-900 focus:ring-2 focus:ring-blue-500 focus:outline-none"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 py-12">
        {/* 채용 공고 섹션 */}
        <section className="py-12">
          <div className="max-w-7xl mx-auto">
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
                <BriefcaseIcon className="h-6 w-6 text-blue-600" />
                최신 채용공고
              </h2>
              <Link 
                href="/job-posting"
                className="text-blue-600 hover:text-blue-800 flex items-center gap-1"
              >
                더 보기
                <ArrowRightIcon className="h-4 w-4" />
              </Link>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {isLoading ? (
                [...Array(3)].map((_, index) => (
                  <div
                    key={`job-skeleton-${index}`}
                    className="bg-white p-6 rounded-lg shadow-lg animate-pulse"
                  >
                    <div className="h-4 bg-gray-200 rounded w-3/4 mb-4"></div>
                    <div className="h-4 bg-gray-200 rounded w-1/2 mb-2"></div>
                    <div className="h-4 bg-gray-200 rounded w-1/4"></div>
                  </div>
                ))
              ) : (
                jobPostings.map((posting) => (
                  <Link 
                    key={posting.id}
                    href={`/job-posting/${posting.id}`}
                    className="block bg-white rounded-lg shadow-lg hover:shadow-xl hover:bg-gray-50 transition-all duration-200 transform hover:-translate-y-1"
                  >
                    <div className="p-6 flex flex-col h-full justify-between">
                      <div>
                        <h2 className="text-lg font-semibold mb-2 line-clamp-2">{posting.subject}</h2>
                        <div className="flex flex-wrap gap-2 mb-4">
                          <span className="px-3 py-1 text-sm font-medium text-blue-600 bg-blue-50 rounded-full">
                            {posting.experienceLevel.name}
                          </span>
                          <span className="px-3 py-1 text-sm font-medium text-green-600 bg-green-50 rounded-full">
                            {posting.salary.name}
                          </span>
                        </div>
                        <div className="space-y-2 text-sm text-gray-600">
                          <p>학력: {posting.requireEducate.name}</p>
                        </div>
                      </div>
                      <div className="flex items-center justify-between text-sm text-gray-500 mt-4">
                        <span>마감일: {formatDate(posting.closeDate)}</span>
                        <span className={`px-2 py-1 rounded ${
                          posting.jobPostingStatus === 'ACTIVE' 
                            ? 'bg-green-100 text-green-800' 
                            : 'bg-red-100 text-red-800'
                        }`}>
                          {posting.jobPostingStatus === 'ACTIVE' ? '진행중' : '마감'}
                        </span>
                      </div>
                    </div>
                  </Link>
                ))
              )}
            </div>
          </div>
        </section>

        {/* 게시글 섹션 */}
        <div>
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
              <ChatBubbleLeftIcon className="h-6 w-6 text-blue-600" />
              최신 게시글
            </h2>
            <Link 
              href="/post"
              className="text-blue-600 hover:text-blue-800 flex items-center gap-1"
            >
              더 보기
              <ArrowRightIcon className="h-4 w-4" />
            </Link>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {isLoading ? (
              [...Array(3)].map((_, index) => (
                <div
                  key={`post-skeleton-${index}`}
                  className="bg-white p-6 rounded-lg shadow-lg animate-pulse"
                >
                  <div className="h-4 bg-gray-200 rounded w-3/4 mb-4"></div>
                  <div className="h-4 bg-gray-200 rounded w-1/2 mb-2"></div>
                  <div className="h-4 bg-gray-200 rounded w-1/4"></div>
                </div>
              ))
            ) : (
              posts.slice(0, 3).map((post) => (
                <Link 
                  key={`main-post-${post.id}`}
                  href={`/post/${post.id}`}
                  className="block bg-white rounded-lg shadow-lg hover:shadow-xl hover:bg-gray-50 transition-all duration-200 transform hover:-translate-y-1"
                >
                  <div className="p-6 flex flex-col h-full justify-between">
                    <div>
                      <h2 className="text-lg font-semibold mb-2 line-clamp-2">{post.subject}</h2>
                      <div className="flex items-center justify-between mb-4">
                        <span className="px-3 py-1 text-sm font-medium text-blue-600 bg-blue-50 rounded-full">
                          {categories.find(cat => cat.id === String(post.categoryId))?.name}
                        </span>
                      </div>
                    </div>
                    <div className="flex items-center justify-between text-sm text-gray-500">
                      <div className="flex items-center gap-2">
                        {post.authorImg ? (
                          <img 
                            src={post.authorImg} 
                            alt={post.authorName}
                            className="w-6 h-6 rounded-full object-cover"
                          />
                        ) : (
                          <div className="w-6 h-6 rounded-full bg-gray-200 flex items-center justify-center">
                            <span className="text-xs text-gray-500">익명</span>
                          </div>
                        )}
                        <span>{post.authorName}</span>
                      </div>
                      <span>{formatDate(post.createdAt)}</span>
                    </div>
                  </div>
                </Link>
              ))
            )}
          </div>
        </div>
      </div>
    </main>
  );
}