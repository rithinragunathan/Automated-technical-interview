import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ShieldAlert, Clock, Code, MessageSquare, ListChecks, Play, Loader2 } from 'lucide-react';
import { getInterview, startInterview } from '../api/interviews';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorMessage from '../components/common/ErrorMessage';
import toast from 'react-hot-toast';

const InterviewInstructionsPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [interview, setInterview] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isStarting, setIsStarting] = useState(false);
  const [agreed, setAgreed] = useState(false);

  useEffect(() => {
    const fetchInterview = async () => {
      try {
        const data = await getInterview(id);
        setInterview(data);
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to load interview instructions');
      } finally {
        setLoading(false);
      }
    };
    fetchInterview();
  }, [id]);

  const handleStart = async () => {
    if (!agreed) return;
    setIsStarting(true);
    try {
      await startInterview(id);
      navigate(`/interviews/${id}/live`);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to start interview');
    } finally {
      setIsStarting(false);
    }
  };

  if (loading) return <LoadingSpinner message="Loading instructions..." />;
  if (error && !interview) return <ErrorMessage message={error} />;

  return (
    <div className="max-w-3xl mx-auto">
      <div className="bg-[#161b22] border border-[#30363d] rounded-lg overflow-hidden">
        <div className="p-6 md:p-8 border-b border-[#30363d]">
          <h1 className="text-2xl font-bold text-white mb-2">Interview Instructions</h1>
          <p className="text-gray-400">Please read carefully before beginning your session.</p>
        </div>

        <div className="p-6 md:p-8 space-y-8">
          {/* Summary */}
          <div className="flex flex-wrap gap-4 bg-[#0f1117] p-4 rounded-md border border-[#30363d]">
            <div className="flex items-center text-gray-300">
              <Clock className="w-5 h-5 mr-2 text-brand-400" />
              <span>Duration: <strong>{Math.ceil((interview.durationSeconds || 2700) / 60)} mins</strong></span>
            </div>
            <div className="flex items-center text-gray-300 ml-0 sm:ml-4">
              {interview.interviewType === 'CODING' ? (
                <Code className="w-5 h-5 mr-2 text-brand-400" />
              ) : (
                <MessageSquare className="w-5 h-5 mr-2 text-brand-400" />
              )}
              <span>Format: <strong>{(interview.interviewType || 'MOCK').replaceAll('_', ' ')}</strong></span>
            </div>
            <div className="flex items-center text-gray-300 ml-0 sm:ml-4">
              <ListChecks className="w-5 h-5 mr-2 text-brand-400" />
              <span>Questions: <strong>{interview.questions?.length || 0}</strong></span>
            </div>
          </div>

          {/* Rules */}
          <div>
            <h2 className="text-lg font-semibold text-white mb-4 flex items-center">
              <ShieldAlert className="w-5 h-5 mr-2 text-yellow-500" />
              Rules and Guidelines
            </h2>
            <ul className="space-y-3 text-sm text-gray-300 list-disc pl-5">
              <li>Ensure you have a stable internet connection before starting.</li>
              <li>Do not refresh the page during the interview, or progress may be lost.</li>
              <li>The timer will start immediately once you click "Begin Interview".</li>
              {interview.interviewType === 'CODING' && (
                <li>Coding assessments include a coding exercise and eight Gemini-generated questions. Answer each question by voice or text.</li>
              )}
              {interview.interviewType === 'MIXED' && (
                <li>Mixed interviews include a coding exercise and four Gemini-generated questions. Answer each question by voice or text.</li>
              )}
              {interview.interviewType === 'TECHNICAL_QA' && (
                <li>Technical Q&A includes eight Gemini-generated questions. Answer each question by voice or text.</li>
              )}
              <li>You must submit your code or answers before the timer runs out.</li>
              <li>Your camera and microphone are <strong>not</strong> required for this automated mock interview.</li>
              <li>Plagiarism checks will be performed on code submissions.</li>
            </ul>
          </div>

          {/* Agreement Checkbox */}
          <div className="bg-brand-900/10 border border-brand-500/30 p-4 rounded-md">
            <label className="flex items-start cursor-pointer">
              <input
                type="checkbox"
                className="mt-1 h-4 w-4 rounded border-gray-600 bg-[#0f1117] text-brand-600 focus:ring-brand-500"
                checked={agreed}
                onChange={(e) => setAgreed(e.target.checked)}
              />
              <span className="ml-3 text-sm text-gray-300">
                I have read the instructions and understand that the timer cannot be paused once started. 
                I am ready to begin the interview.
              </span>
            </label>
          </div>
        </div>

        <div className="p-6 bg-[#0f1117] border-t border-[#30363d] flex justify-end">
          <button
            onClick={() => navigate('/dashboard')}
            className="px-4 py-2 text-sm font-medium text-gray-300 bg-transparent border border-[#30363d] rounded-md hover:bg-[#30363d] mr-3"
            disabled={isStarting}
          >
            Cancel
          </button>
          <button
            onClick={handleStart}
            disabled={!agreed || isStarting}
            className="flex items-center px-6 py-2 text-sm font-medium text-white bg-brand-600 rounded-md hover:bg-brand-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            {isStarting ? (
              <>
                <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                Starting...
              </>
            ) : (
              <>
                <Play className="w-4 h-4 mr-2" />
                Begin Interview
              </>
            )}
          </button>
        </div>
      </div>
    </div>
  );
};

export default InterviewInstructionsPage;
