'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import { MagnifyingGlassIcon } from '@heroicons/react/24/outline';
import privateApi from '@/api/axios';
import { JobPostingPageResponse, JobPostingPage } from '@/types/jobPosting';
import { formatDate } from '@/utils/date';

export default function JobPostingList() {
  const [jobPostings, setJobPostings] = useState<JobPostingPageResponse[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const PAGE_SIZE = 10;

  useEffect(() => {
    setJobPostings([]);
    setPage(0);
    setHasMore(true);
    fetchJobPostings(0, true);
  }, [searchTerm]);

  const fetchJobPostings = async (pageNum: number, reset: boolean = false) => {
    try {
      setIsLoading(true);
      let url = `/api/v1/job-posting?page=${pageNum}&size=${PAGE_SIZE}`;
      
      if (searchTerm) {
        url += `&keyword=${encodeURIComponent(searchTerm)}`;
      }

      const response = await privateApi.get(url);
      
      if (response.data.success) {
        const newPostings = response.data.data.content;
        setJobPostings(prev => reset ? newPostings : [...prev, ...newPostings]);
        setHasMore(!response.data.data.last);
      }
    } catch (error) {
      console.error('채용공고 로딩 실패:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const loadMore = () => {
    if (!isLoading && hasMore) {
      const nextPage = page + 1;
      setPage(nextPage);
      fetchJobPostings(nextPage);
    }
  };

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setJobPostings([]);
    setPage(0);
    setHasMore(true);
    fetchJobPostings(0, true);
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* 상단 배너 */}
      <div className="bg-gradient-to-r from-blue-600 to-blue-800 text-white">
        <div className="max-w-7xl mx-auto py-12 px-8">
          <h1 className="text-4xl font-bold mb-4">채용공고</h1>
          <p className="text-blue-100">다양한 개발자 채용정보를 확인해보세요</p>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-8 -mt-8">
        {/* 검색 카드 */}
        <div className="bg-white rounded-xl shadow-lg p-6 mb-8">
          <form onSubmit={handleSearch} className="flex gap-2">
            <div className="relative flex-1">
              <input
                type="text"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                placeholder="검색어를 입력하세요"
                className="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none"
              />
              <MagnifyingGlassIcon className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
            </div>
            <button
              type="submit"
              className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              검색
            </button>
          </form>
        </div>

        {/* 채용공고 목록 */}
        <div className="space-y-4 mb-8">
          {jobPostings.map((posting) => (
            <Link
              key={posting.id}
              href={`/job-posting/${posting.id}`}
              className="block bg-white rounded-xl shadow-sm hover:shadow-md transition-shadow"
            >
              <div className="p-6">
                <div className="flex justify-between items-start mb-4">
                  <div>
                    <h2 className="text-xl font-semibold mb-2">{posting.subject}</h2>
                    <div className="flex items-center gap-2">
                      <span className="text-gray-600">{posting.experienceLevel.name}</span>
                      <span className="text-gray-400">•</span>
                      <span className="text-gray-600">{posting.requireEducate.name}</span>
                    </div>
                  </div>
                  <div className="flex flex-col items-end gap-2">
                    <span className={`px-3 py-1 rounded-full text-sm font-medium 
                      ${posting.jobPostingStatus === 'ACTIVE' 
                        ? 'bg-green-100 text-green-800' 
                        : 'bg-red-100 text-red-800'}`}
                    >
                      {posting.jobPostingStatus === 'ACTIVE' ? '진행중' : '마감'}
                    </span>
                    <span className="text-blue-600 font-medium">{posting.salary.name}</span>
                  </div>
                </div>
                <div className="flex items-center justify-between text-sm text-gray-500">
                  <div className="flex items-center gap-2">
                    <span>지원자 {posting.applyCnt}명</span>
                  </div>
                  <div className="flex items-center gap-4">
                    <span>시작일: {formatDate(posting.openDate)}</span>
                    <span>마감일: {formatDate(posting.closeDate)}</span>
                  </div>
                </div>
              </div>
            </Link>
          ))}

          {isLoading && (
            [...Array(3)].map((_, index) => (
              <div key={`skeleton-${index}`} className="bg-white p-6 rounded-xl shadow-sm animate-pulse">
                <div className="h-6 bg-gray-200 rounded w-3/4 mb-4"></div>
                <div className="h-4 bg-gray-200 rounded w-1/2 mb-4"></div>
                <div className="flex justify-between">
                  <div className="h-4 bg-gray-200 rounded w-1/4"></div>
                  <div className="h-4 bg-gray-200 rounded w-1/4"></div>
                </div>
              </div>
            ))
          )}
        </div>

        {/* 더보기 버튼 */}
        {!isLoading && hasMore && (
          <div className="flex justify-center mb-8">
            <button
              onClick={loadMore}
              className="px-6 py-3 text-blue-600 bg-white border border-blue-600 rounded-lg hover:bg-blue-50 transition-colors"
            >
              더보기
            </button>
          </div>
        )}
      </div>
    </div>
  );
} 