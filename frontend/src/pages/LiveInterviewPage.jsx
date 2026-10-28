import React, { useCallback, useEffect, useRef, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { Clock, Code, ArrowRight, Loader2 } from 'lucide-react';
import { getInterview, finishInterview } from '../api/interviews';
import { submitAnswer } from '../api/evaluation';
import AIInterviewer from '../components/interview/AIInterviewer';
import QuestionCard from '../components/interview/QuestionCard';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorMessage from '../components/common/ErrorMessage';
import ConfirmDialog from '../components/common/ConfirmDialog';
import toast from 'react-hot-toast';

const LiveInterviewPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [interview, setInterview] = useState(null);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState(null);
  const [timeLeft, setTimeLeft] = useState(0);
  const [timerActive, setTimerActive] = useState(false);
  const [timerReady, setTimerReady] = useState(false);
  const [messages, setMessages] = useState([
    { role: 'assistant', content: "Hello! I'm your mock interviewer. I'll be guiding you through this session today." }
  ]);
  const [isProcessing, setIsProcessing] = useState(false);
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [currentQuestion, setCurrentQuestion] = useState(null);
  const [isFinishing, setIsFinishing] = useState(false);
  const [showEndDialog, setShowEndDialog] = useState(false);
  const finishRequested = useRef(false);

  useEffect(() => {
    let isCurrentRequest = true;
    finishRequested.current = false;
    const initInterview = async () => {
      setLoading(true);
      setLoadError(null);
      try {
        const data = await getInterview(id);
        if (!isCurrentRequest) return;
        if (data.status === 'PENDING') {
          navigate(`/interviews/${id}/instructions`, { replace: true });
          return;
        }
        if (data.status === 'COMPLETED') {
          navigate(`/interviews/${id}/report`, { replace: true });
          return;
        }

        const questions = (Array.isArray(data.questions) ? data.questions : []).map(question => ({
          ...question,
          text: question.questionText || question.text || ''
        }));
        if (questions.some(question => !question.text.trim())) {
          throw new Error('This interview contains a question without text.');
        }
        setInterview({ ...data, questions });
        const firstUnansweredIndex = questions.findIndex(question => !question.completedAt);
        const nextIndex = firstUnansweredIndex < 0 ? questions.length : firstUnansweredIndex;
        setCurrentQuestionIndex(nextIndex);
        setCurrentQuestion(questions[nextIndex] || null);

        const duration = Math.max(1, Number(data.durationSeconds) || 2700);
        // The backend serializes LocalDateTime without an offset; Docker runs the
        // service in UTC, so interpret that value as UTC rather than browser-local time.
        const startTime = data.startTime
          ? `${data.startTime}${/[zZ]|[+-]\d{2}:\d{2}$/.test(data.startTime) ? '' : 'Z'}`
          : null;
        const startedAt = startTime ? new Date(startTime).getTime() : Date.now();
        const elapsedSeconds = Math.max(0, Math.floor((Date.now() - startedAt) / 1000));
        const remainingSeconds = Math.max(0, duration - elapsedSeconds);
        setTimeLeft(remainingSeconds);
        setTimerActive(data.status === 'ACTIVE');
        setTimerReady(true);
      } catch (error) {
        console.error('Failed to load interview:', error);
        if (isCurrentRequest) {
          setLoadError(error.message || 'Failed to load this interview. Please return to your interview history and try again.');
        }
      } finally {
        if (isCurrentRequest) setLoading(false);
      }
    };
    initInterview();
    return () => { isCurrentRequest = false; };
  }, [id, navigate]);

  const handleFinish = useCallback(async (forced = false) => {
    if (finishRequested.current) return;
    finishRequested.current = true;
    setIsFinishing(true);
    try {
      await finishInterview(id);
      setTimerActive(false);
      toast.success(forced ? 'Time is up! Interview ended.' : 'Interview completed successfully!');
      navigate(`/interviews/${id}/report`);
    } catch (error) {
      finishRequested.current = false;
      console.error('Failed to finish interview:', error);
      toast.error(error.response?.data?.message || 'Failed to finish interview. Please try again.');
    } finally {
      setIsFinishing(false);
    }
  }, [id, navigate]);

  useEffect(() => {
    const questions = interview?.questions || [];
    if (
      interview?.status === 'ACTIVE'
      && questions.length > 0
      && questions.every(question => question.completedAt)
      && !isFinishing
    ) {
      handleFinish(false);
    }
  }, [handleFinish, interview, isFinishing]);

  useEffect(() => {
    if (!timerReady || !timerActive) return undefined;
    const interval = setInterval(() => {
      setTimeLeft(seconds => Math.max(0, seconds - 1));
    }, 1000);
    return () => clearInterval(interval);
  }, [timerReady, timerActive]);

  useEffect(() => {
    if (timerReady && timerActive && timeLeft === 0) {
      handleFinish(true);
    }
  }, [timerReady, timerActive, timeLeft, handleFinish]);

  const formatTime = (seconds) => {
    const minutes = Math.floor(seconds / 60).toString().padStart(2, '0');
    const remainingSeconds = (seconds % 60).toString().padStart(2, '0');
    return `${minutes}:${remainingSeconds}`;
  };

  const handleSendMessage = async (text) => {
    if (!currentQuestion || !text.trim() || isProcessing) return false;
    setMessages(previous => [...previous, { role: 'user', content: text }]);
    setIsProcessing(true);

    try {
      const response = await submitAnswer(id, currentQuestion.id, text);
      setMessages(previous => [...previous, {
        role: 'assistant',
        content: response.feedback
      }]);

      const questions = interview?.questions || [];
      setInterview(previous => ({
        ...previous,
        questions: previous.questions.map(question => (
          question.id === currentQuestion.id
            ? { ...question, completedAt: new Date().toISOString() }
            : question
        ))
      }));
      const nextIndex = currentQuestionIndex + 1;
      setCurrentQuestionIndex(nextIndex);
      setCurrentQuestion(questions[nextIndex] || null);
      return true;
    } catch (error) {
      console.error('Failed to submit interview answer:', error);
      toast.error(error.response?.data?.message || 'Failed to submit your answer. Please try again.');
      return false;
    } finally {
      setIsProcessing(false);
    }
  };

  if (loading) return <LoadingSpinner fullScreen message="Preparing interview environment..." />;
  if (loadError) return <div className="mx-auto max-w-2xl p-6"><ErrorMessage message={loadError} /></div>;
  if (!interview) return null;

  const questions = interview.questions || [];
  const isCodingQuestion = Boolean(currentQuestion?.codingProblemId);
  const isLowTime = timeLeft < 300;
  const progress = Math.min(currentQuestion ? currentQuestionIndex + 1 : questions.length, questions.length);

  return (
    <div className="h-screen flex flex-col bg-[#0f1117]">
      <header className="h-16 border-b border-[#30363d] bg-[#161b22] px-6 flex items-center justify-between shrink-0">
        <h1 className="text-lg font-semibold text-white tracking-tight">
          Live Session <span className="mx-2 text-[#30363d]">|</span>
          <span className="text-gray-400 font-normal">{interview.topic || 'Interview'}</span>
        </h1>
        <div className="flex items-center space-x-6">
          <div className="text-sm font-medium text-gray-400">
            Progress: <span className="text-white">{progress}/{questions.length}</span>
          </div>
          <div className={`flex items-center text-lg font-mono font-bold ${isLowTime ? 'text-red-500 animate-pulse' : 'text-brand-400'}`}>
            <Clock className="w-5 h-5 mr-2" />
            {formatTime(timeLeft)}
          </div>
          <button
            onClick={() => setShowEndDialog(true)}
            disabled={isFinishing}
            className="px-4 py-2 bg-red-900/20 text-red-500 hover:bg-red-900/40 border border-red-900/50 rounded-md text-sm font-medium transition-colors disabled:opacity-50"
          >
            End Interview
          </button>
        </div>
      </header>

      <main className="flex-grow flex flex-col md:flex-row overflow-hidden p-4 gap-4">
        <div className="w-full md:w-1/3 flex flex-col h-full min-h-0">
          {isCodingQuestion ? (
            <div className="h-full bg-[#161b22] border border-[#30363d] rounded-lg p-6">
              <Code className="w-8 h-8 text-brand-400 mb-4" />
              <h2 className="text-lg font-semibold text-white mb-2">Coding assessment</h2>
              <p className="text-gray-300">{currentQuestion.questionText}</p>
              <p className="text-sm text-gray-500 mt-4">Solve the assigned problem in the IDE, then return here to continue.</p>
            </div>
          ) : (
            <AIInterviewer
              currentQuestion={currentQuestion}
              messages={messages}
              onSendMessage={handleSendMessage}
              isProcessing={isProcessing}
              isComplete={!currentQuestion}
              inputEnabled={false}
            />
          )}
        </div>

        <div className="w-full md:w-2/3 flex flex-col h-full min-h-0">
          {isCodingQuestion ? (
            <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-8 flex flex-col items-center justify-center h-full text-center">
              <Code className="w-16 h-16 text-gray-500 mb-4" />
              <h2 className="text-xl font-bold text-white mb-2">{currentQuestion.questionText}</h2>
              <p className="text-gray-400 mb-6 max-w-md">
                Open the assigned coding problem in the IDE. Your submission will be linked to this interview.
              </p>
              <Link
                to={`/problems/${currentQuestion.codingProblemId}?interviewId=${id}`}
                className="px-6 py-3 bg-brand-600 hover:bg-brand-500 text-white font-medium rounded-md flex items-center transition-colors"
              >
                Open assigned problem <ArrowRight className="w-5 h-5 ml-2" />
              </Link>
            </div>
          ) : currentQuestion ? (
            <QuestionCard
              question={currentQuestion}
              onSubmitAnswer={handleSendMessage}
              isSubmitting={isProcessing}
            />
          ) : (
            <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-8 flex flex-col items-center justify-center h-full text-center">
              <h2 className="text-xl font-bold text-white mb-2">Ready to finish?</h2>
              <p className="text-gray-400 mb-6">Your answers are saved. Finish the interview to generate your report.</p>
              <button
                type="button"
                onClick={() => handleFinish(false)}
                disabled={isFinishing}
                className="px-6 py-3 bg-brand-600 hover:bg-brand-500 text-white font-medium rounded-md flex items-center disabled:opacity-50"
              >
                {isFinishing && <Loader2 className="w-5 h-5 mr-2 animate-spin" />}
                Finish interview
              </button>
            </div>
          )}
        </div>
      </main>

      <ConfirmDialog
        isOpen={showEndDialog}
        title="End Interview"
        message="Are you sure you want to end this interview? You cannot return to it once ended."
        confirmText="Yes, End Interview"
        cancelText="Resume"
        isDanger={true}
        onConfirm={() => { setShowEndDialog(false); handleFinish(false); }}
        onCancel={() => setShowEndDialog(false)}
      />
    </div>
  );
};

export default LiveInterviewPage;
