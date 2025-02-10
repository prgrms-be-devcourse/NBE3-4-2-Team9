'use client';

import { useRouter } from 'next/navigation';
import { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '@/store/store';
import { ArrowLeftIcon, ExclamationCircleIcon } from '@heroicons/react/24/outline';
import privateApi from '@/api/axios';
// @ts-ignore
import { Category } from '@/types/category';

export default function WritePost() {
  const router = useRouter();
  const { user } = useSelector((state: RootState) => state.auth);
  const [subject, setSubject] = useState('');
  const [content, setContent] = useState('');
  const [category, setCategory] = useState('');
  const [categories, setCategories] = useState<Category[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [charCount, setCharCount] = useState(0);
  const MAX_CONTENT_LENGTH = 10000;

  useEffect(() => {
    if (!user) {
      router.push('/login');
      return;
    }

    const fetchCategories = async () => {
      try {
        setIsLoading(true);
        const response = await privateApi.get('/api/v1/category');
        if (response.data.success) {
          const categoryData = response.data.data;
          setCategories(categoryData);
          if (categoryData.length > 0) {
            setCategory(categoryData[0].id.toString());
          }
        }
      } catch (error) {
        console.error('카테고리 조회 실패:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchCategories();
  }, [user, router]);

  const handleContentChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    const text = e.target.value;
    if (text.length <= MAX_CONTENT_LENGTH) {
      setContent(text);
      setCharCount(text.length);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!subject.trim() || !content.trim() || !category) {
      alert('모든 필드를 입력해주세요.');
      return;
    }

    try {
      const isAdmin = user?.email?.includes('admin');
      const postData = {
        subject: subject.trim(),
        content: content.trim(),
        categoryId: parseInt(category),
        authorId: user?.id,
        authorName: isAdmin ? '관리자' : user?.nickname,
        isAdmin: isAdmin
      };

      const response = await privateApi.post('/api/v1/posts', postData);

      if (response.data.success) {
        alert('게시글이 작성되었습니다.');
        router.push('/post');
      } else {
        throw new Error(response.data.message || '게시글 작성에 실패했습니다.');
      }
    } catch (error: any) {
      console.error('게시글 작성 실패:', error);
      alert(error.response?.data?.message || '게시글 작성에 실패했습니다.');
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* 상단 네비게이션 */}
      <div className="bg-white border-b">
        <div className="max-w-screen-xl mx-auto px-4">
          <div className="flex items-center h-16">
            <button
              onClick={() => router.back()}
              className="flex items-center gap-2 text-gray-600 hover:text-blue-600 transition-colors"
            >
              <ArrowLeftIcon className="h-5 w-5" />
              목록으로
            </button>
          </div>
        </div>
      </div>

      <div className="max-w-screen-xl mx-auto px-4 py-8">
        <div className="mb-6">
          <h1 className="text-2xl font-bold text-gray-900">새 게시글 작성</h1>
          <p className="mt-2 text-gray-600">커뮤니티 가이드라인을 준수하여 작성해주세요.</p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
          {/* 작성 가이드 */}
          <div className="lg:col-span-1 order-2 lg:order-1">
            <div className="bg-white rounded-xl shadow-sm p-6 sticky top-6">
              <h3 className="text-sm font-semibold text-gray-900 mb-4">게시글 작성 가이드</h3>
              <ul className="space-y-3 text-sm text-gray-600">
                <li className="flex items-start gap-2">
                  <ExclamationCircleIcon className="h-5 w-5 text-blue-500 flex-shrink-0 mt-0.5" />
                  <span>제목은 명확하고 간단하게 작성해주세요.</span>
                </li>
                <li className="flex items-start gap-2">
                  <ExclamationCircleIcon className="h-5 w-5 text-blue-500 flex-shrink-0 mt-0.5" />
                  <span>내용은 상세하게 기술하되, 불필요한 정보는 제외해주세요.</span>
                </li>
                <li className="flex items-start gap-2">
                  <ExclamationCircleIcon className="h-5 w-5 text-blue-500 flex-shrink-0 mt-0.5" />
                  <span>타인을 비방하거나 불쾌감을 주는 내용은 삼가해주세요.</span>
                </li>
              </ul>
            </div>
          </div>

          {/* 메인 폼 */}
          <div className="lg:col-span-3 order-1 lg:order-2">
            <form onSubmit={handleSubmit} className="bg-white rounded-xl shadow-sm">
              <div className="p-6 border-b">
                <div className="grid grid-cols-1 md:grid-cols-4 gap-4 items-start">
                  <div className="md:col-span-1">
                    <label className="block text-sm font-medium text-gray-700">
                      카테고리
                    </label>
                  </div>
                  <div className="md:col-span-3">
                    {isLoading ? (
                      <div className="w-full h-10 bg-gray-100 rounded-lg animate-pulse" />
                    ) : (
                      <select
                        value={category}
                        onChange={(e) => setCategory(e.target.value)}
                        className="w-full px-4 py-2.5 bg-white border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none text-gray-900"
                        required
                      >
                        <option value="">카테고리를 선택하세요</option>
                        {categories.map((cat) => (
                          <option key={cat.id} value={cat.id}>
                            {cat.name}
                          </option>
                        ))}
                      </select>
                    )}
                  </div>
                </div>
              </div>

              <div className="p-6 border-b">
                <div className="grid grid-cols-1 md:grid-cols-4 gap-4 items-start">
                  <div className="md:col-span-1">
                    <label className="block text-sm font-medium text-gray-700">
                      제목
                    </label>
                  </div>
                  <div className="md:col-span-3">
                    <input
                      type="text"
                      value={subject}
                      onChange={(e) => setSubject(e.target.value)}
                      className="w-full px-4 py-2.5 bg-white border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none"
                      placeholder="제목을 입력하세요"
                      required
                    />
                  </div>
                </div>
              </div>

              <div className="p-6">
                <div className="grid grid-cols-1 md:grid-cols-4 gap-4 items-start">
                  <div className="md:col-span-1">
                    <label className="block text-sm font-medium text-gray-700">
                      내용
                    </label>
                  </div>
                  <div className="md:col-span-3">
                    <textarea
                      value={content}
                      onChange={handleContentChange}
                      rows={15}
                      className="w-full px-4 py-3 bg-white border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none resize-none"
                      placeholder="내용을 입력하세요"
                      required
                    />
                    <div className="mt-2 flex justify-end">
                      <span className={`text-sm ${charCount > MAX_CONTENT_LENGTH * 0.9 ? 'text-red-500' : 'text-gray-500'}`}>
                        {charCount.toLocaleString()} / {MAX_CONTENT_LENGTH.toLocaleString()}자
                      </span>
                    </div>
                  </div>
                </div>
              </div>

              <div className="px-6 py-4 bg-gray-50 rounded-b-xl border-t">
                <div className="flex justify-end gap-3">
                  <button
                    type="button"
                    onClick={() => router.back()}
                    className="px-5 py-2.5 text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
                  >
                    취소
                  </button>
                  <button
                    type="submit"
                    className="px-5 py-2.5 text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition-colors flex items-center gap-2 disabled:opacity-50"
                    disabled={isLoading || !category || !subject.trim() || !content.trim()}
                  >
                    {isLoading ? '로딩중...' : '작성하기'}
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
} 