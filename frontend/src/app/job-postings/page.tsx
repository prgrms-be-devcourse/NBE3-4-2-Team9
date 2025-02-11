'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { getJobPostings } from '@/api/jobposting';
import { JobPosting, JobPostingSearchCondition } from '@/types/jobposting';
import { formatDate } from '@/utils/dateUtils';

export default function JobPostings() {
  const router = useRouter();
  const [jobPostings, setJobPostings] = useState<JobPosting[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchCondition, setSearchCondition] = useState<JobPostingSearchCondition>({
    pageNum: 0,
    pageSize: 10
  });

  useEffect(() => {
    fetchJobPostings();
  }, [searchCondition]);

  const fetchJobPostings = async () => {
    try {
      setIsLoading(true);
      const response = await getJobPostings(searchCondition);
      if (response.success) {
        setJobPostings(response.data.content);
      }
    } catch (error) {
      console.error('Failed to fetch job postings:', error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4">
        <h1 className="text-2xl font-bold mb-6">채용공고</h1>
        
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {jobPostings.map((posting) => (
            <div 
              key={posting.id}
              className="bg-white rounded-lg shadow-md p-6 cursor-pointer hover:shadow-lg transition-shadow"
              onClick={() => router.push(`/job-postings/${posting.id}`)}
            >
              <h2 className="text-xl font-semibold mb-3 line-clamp-2">{posting.subject}</h2>
              <div className="space-y-2 text-sm text-gray-600">
                <p>경력: {posting.experienceLevel.name}</p>
                <p>학력: {posting.requireEducate.name}</p>
                <p>연봉: {posting.salary.name}</p>
                <p>마감일: {formatDate(posting.closeDate)}</p>
              </div>
              <div className="mt-4 flex justify-between items-center text-sm">
                <span className="text-blue-600">지원자 {posting.applyCnt}명</span>
                <span className={`px-2 py-1 rounded ${
                  posting.jobPostingStatus === 'ACTIVE' 
                    ? 'bg-green-100 text-green-800' 
                    : 'bg-red-100 text-red-800'
                }`}>
                  {posting.jobPostingStatus === 'ACTIVE' ? '진행중' : '마감'}
                </span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
} 