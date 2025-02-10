'use client';

import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { getPost } from '@/api/post';
import { PostResponse } from '@/types/post/PostResponse';
import { formatDate } from '@/utils/dateUtils';
import { ArrowLeftIcon } from '@heroicons/react/24/outline';
import { Category } from '@/types/post/Category';
import { getCategories } from '@/api/category';
import Link from 'next/link';
import privateApi from '@/api/axios';
import { useSelector } from 'react-redux';
import { RootState } from '@/store/store';

interface Comment {
  id: number;
  content: string;
  createdAt: string;
  modifiedAt: string;
  authorName: string;
  authorImg?: string;
}

export default function PostDetail() {
  const params = useParams();
  const router = useRouter();
  const [post, setPost] = useState<PostResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [categories, setCategories] = useState<Category[]>([]);
  const [isAuthor, setIsAuthor] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [editedSubject, setEditedSubject] = useState('');
  const [editedContent, setEditedContent] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { user } = useSelector((state: RootState) => state.auth);
  const [comments, setComments] = useState<Comment[]>([]);
  const [newComment, setNewComment] = useState('');
  const [isSubmittingComment, setIsSubmittingComment] = useState(false);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await getCategories();
        if (response.success) {
          console.log('Categories:', response.data);
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
        const [postResponse, categoriesResponse] = await Promise.all([
          privateApi.get(`/api/v1/posts/${params.id}`),
          privateApi.get('/api/v1/category')
        ]);

        if (postResponse.data.success) {
          setPost(postResponse.data.data);
          setEditedSubject(postResponse.data.data.subject);
          setEditedContent(postResponse.data.data.content);
          const currentUserId = localStorage.getItem('userId');
          setIsAuthor(currentUserId === String(postResponse.data.data.authorId));
        }
        if (categoriesResponse.data.success) {
          setCategories(categoriesResponse.data.data);
        }

        // 댓글은 게시글 조회가 성공한 후에 가져오기
        try {
          const commentsResponse = await privateApi.get(`/api/v1/posts/${params.id}/comments`);
          if (commentsResponse.data.success) {
            setComments(commentsResponse.data.data);
          }
        } catch (error) {
          console.error('Failed to fetch comments:', error);
        }
      } catch (error) {
        console.error('Failed to fetch data:', error);
      } finally {
        setIsLoading(false);
      }
    };

    if (params.id) {
      fetchData();
    }
  }, [params.id]);

  const handleDelete = async () => {
    if (!confirm('정말로 이 게시글을 삭제하시겠습니까?')) {
      return;
    }

    try {
      setIsDeleting(true);
      const response = await privateApi.delete(`/api/v1/posts/${params.id}`);
      
      if (response.data.success) {
        alert('게시글이 삭제되었습니다.');
        router.push('/post');
        router.refresh();
      }
    } catch (error: any) {
      if (error.response?.status === 403) {
        alert('본인이 작성한 게시글만 삭제할 수 있습니다.');
      } else {
        alert('게시글 삭제 중 오류가 발생했습니다.');
      }
    } finally {
      setIsDeleting(false);
    }
  };

  const handleEdit = () => {
    setIsEditing(true);
  };

  const handleCancel = () => {
    setIsEditing(false);
    setEditedSubject(post?.subject || '');
    setEditedContent(post?.content || '');
  };

  const handleUpdate = async () => {
    if (!post || !editedSubject.trim() || !editedContent.trim()) return;

    try {
      setIsSubmitting(true);
      const response = await privateApi.put(`/api/v1/posts/${post.id}`, {
        subject: editedSubject,
        content: editedContent,
        categoryId: post.categoryId
      });

      if (response.data.success) {
        setPost({
          ...post,
          subject: editedSubject,
          content: editedContent
        });
        setIsEditing(false);
        alert('게시글이 수정되었습니다.');
      }
    } catch (error) {
      console.error('Failed to update post:', error);
      alert('게시글 수정에 실패했습니다.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleCommentSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!newComment.trim()) {
      alert('댓글 내용을 입력해주세요.');
      return;
    }

    try {
      setIsSubmittingComment(true);
      const response = await privateApi.post(`/api/v1/posts/${params.id}/comments`, {
        content: newComment.trim()
      });

      if (response.data.success) {
        setNewComment('');
        setComments(prev => [...prev, response.data.data]);
      }
    } catch (error) {
      console.error('댓글 작성 실패:', error);
      alert('댓글 작성에 실패했습니다.');
    } finally {
      setIsSubmittingComment(false);
    }
  };

  if (isLoading) {
    return (
      <div className="max-w-4xl mx-auto p-8">
        <div className="animate-pulse">
          <div className="h-8 bg-gray-200 rounded w-3/4 mb-4"></div>
          <div className="h-4 bg-gray-200 rounded w-1/4 mb-8"></div>
          <div className="space-y-4">
            <div className="h-4 bg-gray-200 rounded w-full"></div>
            <div className="h-4 bg-gray-200 rounded w-full"></div>
            <div className="h-4 bg-gray-200 rounded w-3/4"></div>
          </div>
        </div>
      </div>
    );
  }

  if (!post) {
    return (
      <div className="max-w-4xl mx-auto p-8">
        <div className="text-center py-12 text-gray-500">
          로그인 후 이용해주세요.
        </div>
      </div>
    );
  }

  const categoryName = categories.find(cat => Number(cat.id) === post.categoryId)?.name;

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-white border-b">
        <div className="max-w-4xl mx-auto px-4">
          <div className="flex items-center py-4 text-sm">
            <button
              onClick={() => router.back()}
              className="flex items-center gap-2 text-gray-600 hover:text-blue-600 transition-colors"
            >
              <ArrowLeftIcon className="h-5 w-5" />
              목록으로
            </button>
            <div className="mx-4 text-gray-300">|</div>
            <div className="flex items-center text-gray-600">
              현재 게시판:
              <Link 
                href={`/post?category=${post.categoryId}`}
                className="ml-2 text-blue-600 hover:text-blue-700 transition-colors font-medium"
              >
                {categoryName}
              </Link>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-4xl mx-auto px-4 py-8">
        <article className="bg-white rounded-xl shadow-lg overflow-hidden">
          <div className="p-8">
            <div className="mb-8">
              <h1 className="text-2xl font-bold text-gray-900 mb-3">
                {post.subject}
              </h1>
              <div className="flex items-center justify-between text-sm text-gray-500 mb-8">
                <div className="flex items-center gap-4">
                  <div className="flex items-center gap-2">
                    {post.authorImg ? (
                      <img 
                        src={post.authorImg} 
                        alt={post.authorName}
                        className="w-8 h-8 rounded-full object-cover"
                      />
                    ) : (
                      <div className="w-8 h-8 rounded-full bg-gray-200 flex items-center justify-center">
                        <span className="text-sm text-gray-500">익명</span>
                      </div>
                    )}
                    <span>{post.authorName}</span>
                  </div>
                  <span>{formatDate(post.createdAt)}</span>
                </div>
              </div>
            </div>

            <div className="prose max-w-none text-gray-800 leading-relaxed mb-8">
              {isEditing ? (
                <div className="space-y-4">
                  <input
                    type="text"
                    value={editedSubject}
                    onChange={(e) => setEditedSubject(e.target.value)}
                    className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                  <textarea
                    value={editedContent}
                    onChange={(e) => setEditedContent(e.target.value)}
                    className="w-full h-64 p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
                  />
                  <div className="flex gap-2 justify-end">
                    <button
                      onClick={handleUpdate}
                      disabled={isSubmitting}
                      className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition-colors"
                    >
                      {isSubmitting ? '저장 중...' : '저장'}
                    </button>
                    <button
                      onClick={handleCancel}
                      className="px-4 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300 transition-colors"
                    >
                      취소
                    </button>
                  </div>
                </div>
              ) : (
                <>
                  <h1 className="text-2xl font-bold mb-4">{post.subject}</h1>
                  <div className="whitespace-pre-wrap">{post.content}</div>
                </>
              )}
            </div>

            <div className="flex items-center justify-between pt-6 border-t">
              <div className="flex items-center gap-4">
                <button className="flex items-center gap-2 text-gray-500 hover:text-blue-600 transition-colors">
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14 10h4.764a2 2 0 011.789 2.894l-3.5 7A2 2 0 0115.263 21h-4.017c-.163 0-.326-.02-.485-.06L7 20m7-10V5a2 2 0 00-2-2h-.095c-.5 0-.905.405-.905.905 0 .714-.211 1.412-.608 2.006L7 11v9m7-10h-2M7 20H5a2 2 0 01-2-2v-6a2 2 0 012-2h2.5" />
                  </svg>
                  <span>추천</span>
                </button>
                <button className="flex items-center gap-2 text-gray-500 hover:text-blue-600 transition-colors">
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
                  </svg>
                  <span>댓글</span>
                </button>
              </div>
              <div className="flex items-center gap-2">
                <button 
                  className="px-4 py-2 text-sm text-gray-600 hover:text-gray-600 transition-colors"
                  onClick={handleEdit}
                >
                  수정
                </button>
                <button 
                  className="px-4 py-2 text-sm text-gray-600 hover:text-red-600 transition-colors"
                  onClick={handleDelete}
                  disabled={isDeleting}
                >
                  {isDeleting ? '삭제 중...' : '삭제'}
                </button>
              </div>
            </div>
          </div>
        </article>

        <div className="mt-8 bg-white rounded-xl shadow-lg p-6">
          <h3 className="text-lg font-bold mb-4">댓글</h3>
          
          <form onSubmit={handleCommentSubmit} className="mb-6">
            <div className="flex flex-col gap-2">
              <textarea
                value={newComment}
                onChange={(e) => setNewComment(e.target.value)}
                placeholder="댓글을 입력하세요"
                className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
                rows={3}
              />
              <div className="flex justify-end">
                <button
                  type="submit"
                  disabled={isSubmittingComment}
                  className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:bg-gray-400"
                >
                  {isSubmittingComment ? '작성 중...' : '댓글 작성'}
                </button>
              </div>
            </div>
          </form>

          <div className="space-y-4">
            {comments.map((comment) => (
              <div key={comment.id} className="p-4 bg-gray-50 rounded-lg">
                <div className="flex items-center justify-between mb-2">
                  <div className="flex items-center gap-2">
                    {comment.authorImg ? (
                      <img 
                        src={comment.authorImg} 
                        alt={comment.authorName}
                        className="w-8 h-8 rounded-full object-cover"
                      />
                    ) : (
                      <div className="w-8 h-8 rounded-full bg-gray-200 flex items-center justify-center">
                        <span className="text-xs text-gray-500">익명</span>
                      </div>
                    )}
                    <span className="font-medium text-gray-700">{comment.authorName}</span>
                    <span className="text-sm text-gray-500">
                      {formatDate(comment.createdAt)}
                    </span>
                  </div>
                </div>
                <p className="text-gray-800 whitespace-pre-wrap">{comment.content}</p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
} 