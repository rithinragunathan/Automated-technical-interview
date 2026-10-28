import React, { useState, useEffect, useRef } from 'react';
import { useParams, Link, useNavigate, useSearchParams } from 'react-router-dom';
import { Play, Send, RefreshCw, ChevronLeft } from 'lucide-react';
import CodeEditor from '../components/coding/CodeEditor';
import ProblemPanel from '../components/coding/ProblemPanel';
import TestCasePanel from '../components/coding/TestCasePanel';
import ExecutionStatus from '../components/coding/ExecutionStatus';
import LoadingSpinner from '../components/common/LoadingSpinner';
import { getProblem } from '../api/problems';
import { createSubmission, runCode } from '../api/submissions';
import { SUBMISSION_STATUS, LANGUAGES } from '../utils/constants';
import toast from 'react-hot-toast';

const CodingPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [problem, setProblem] = useState(null);
  const [loading, setLoading] = useState(true);
  
  // Editor State
  const [language, setLanguage] = useState(LANGUAGES[0]);
  const [code, setCode] = useState('');
  
  // Execution State
  const [status, setStatus] = useState(SUBMISSION_STATUS.IDLE);
  const [results, setResults] = useState(null);
  const [metrics, setMetrics] = useState(null);
  const [executionSummary, setExecutionSummary] = useState(null);

  const interviewId = searchParams.get('interviewId');
  const getStarterCode = (data, lang) => {
    const starterCodeByLanguage = {
      JAVA: data?.starterCodeJava,
      PYTHON: data?.starterCodePython,
      CPP: data?.starterCodeCpp
    };

    return starterCodeByLanguage[lang.id] || '// Write your code here\n';
  };

  useEffect(() => {
    const fetchProblem = async () => {
      try {
        const data = await getProblem(id);
        setProblem(data);
        // Set initial code based on language
        setCode(getStarterCode(data, language));
      } catch (err) {
        console.error('Failed to load problem:', err);
        toast.error('Failed to load problem');
      } finally {
        setLoading(false);
      }
    };
    fetchProblem();
  }, [id]);

  const handleRun = async (isSubmit = false) => {
    if (!code.trim()) {
      toast.error('Code cannot be empty');
      return;
    }
    
    setStatus(SUBMISSION_STATUS.SUBMITTING);
    setResults(null);
    setMetrics(null);
    setExecutionSummary(null);
    
    try {
      const request = {
        problemId: problem.id,
        interviewId: interviewId ? Number(interviewId) : null,
        language: language.id,
        sourceCode: code,
        executionType: isSubmit ? 'SUBMIT' : 'RUN'
      };
      const response = isSubmit ? await createSubmission(request) : await runCode(request);
      
      const testCaseById = new Map((problem.publicTestCases || []).map(testCase => [testCase.id, testCase]));
      const testResults = Array.isArray(response.testResults) ? response.testResults : [];
      const responseStatus = typeof response.status === 'string' ? response.status : 'UNKNOWN';
      setResults(testResults.map(result => ({
        status: result.passed ? 'PASSED' : result.errorMessage ? 'ERROR' : 'FAILED',
        input: testCaseById.get(result.testCaseId)?.input || 'Hidden test case',
        expectedOutput: testCaseById.get(result.testCaseId)?.expectedOutput || 'Hidden test case',
        actualOutput: result.actualOutput,
        errorMessage: result.errorMessage,
        consoleOutput: result.actualOutput || result.errorMessage || ''
      })));
      setMetrics({ timeMs: response.executionTimeMs, memoryKb: response.memoryUsedKb });
      setExecutionSummary({
        status: responseStatus,
        passedTests: Number.isFinite(response.passedTests)
          ? response.passedTests
          : testResults.filter(result => result.passed).length,
        totalTests: Number.isFinite(response.totalTests) ? response.totalTests : testResults.length
      });
      setStatus(
        ['COMPILATION_ERROR', 'RUNTIME_ERROR', 'TIME_LIMIT_EXCEEDED', 'MEMORY_LIMIT_EXCEEDED', 'SYSTEM_ERROR']
          .includes(responseStatus)
          ? SUBMISSION_STATUS.ERROR
          : SUBMISSION_STATUS.DONE
      );

      if (isSubmit && interviewId && responseStatus === 'ACCEPTED') {
        toast.success('Solution accepted. Returning to your interview.');
        navigate(`/interviews/${interviewId}/live`);
      }
      
    } catch (err) {
      console.error(err);
      setStatus(SUBMISSION_STATUS.ERROR);
      toast.error('Execution failed to start');
    }
  };

  const handleLanguageChange = (e) => {
    const lang = LANGUAGES.find(l => l.id === e.target.value);
    setLanguage(lang);
    setCode(getStarterCode(problem, lang));
  };

  const handleReset = () => {
    if (window.confirm('Are you sure you want to reset your code? This cannot be undone.')) {
      setCode(getStarterCode(problem, language));
    }
  };

  if (loading) return <LoadingSpinner fullScreen message="Loading IDE..." />;

  return (
    <div className="h-screen flex flex-col bg-[#0f1117] overflow-hidden">
      {/* Header */}
      <header className="h-14 bg-[#161b22] border-b border-[#30363d] flex items-center justify-between px-4 shrink-0">
        <div className="flex items-center">
          <Link
            to={interviewId ? `/interviews/${interviewId}/live` : '/problems'}
            className="mr-4 text-gray-400 hover:text-white transition-colors"
            aria-label={interviewId ? 'Return to interview' : 'Return to problem library'}
          >
            <ChevronLeft className="w-5 h-5" />
          </Link>
          <span className="font-semibold text-white tracking-tight">{problem?.title}</span>
        </div>
        <div className="flex items-center space-x-4">
          <ExecutionStatus
            status={status}
            message={executionSummary
              ? `${executionSummary.status.replaceAll('_', ' ')} · ${executionSummary.passedTests}/${executionSummary.totalTests} tests passed`
              : undefined}
          />
        </div>
      </header>

      {/* Main Layout */}
      <main className="flex-grow flex min-h-0 overflow-hidden">
        
        {/* Left Panel: Problem Description */}
        <div className="w-[40%] flex-shrink-0 relative">
          <ProblemPanel problem={problem} />
          {/* Resizer could go here */}
        </div>
        
        {/* Right Panel: Editor & Output */}
        <div className="w-[60%] flex flex-col min-w-[300px] min-h-0">
          {/* Editor Toolbar */}
          <div className="h-12 bg-[#161b22] border-b border-[#30363d] flex items-center justify-between px-4 shrink-0">
            <div className="flex items-center">
              <select 
                value={language.id} 
                onChange={handleLanguageChange}
                className="bg-[#0f1117] border border-[#30363d] text-gray-300 text-sm rounded-md px-2 py-1 focus:outline-none focus:border-brand-500"
              >
                {LANGUAGES.map(lang => (
                  <option key={lang.id} value={lang.id}>{lang.name}</option>
                ))}
              </select>
              <button 
                onClick={handleReset}
                className="ml-3 p-1.5 text-gray-400 hover:text-white rounded-md hover:bg-[#30363d] transition-colors"
                title="Reset Code"
              >
                <RefreshCw className="w-4 h-4" />
              </button>
            </div>
            <div className="flex items-center space-x-3">
              <button
                onClick={() => handleRun(false)}
                disabled={status !== SUBMISSION_STATUS.IDLE && status !== SUBMISSION_STATUS.DONE && status !== SUBMISSION_STATUS.ERROR}
                className="px-4 py-1.5 bg-[#30363d] hover:bg-[#484f58] text-gray-200 text-sm font-medium rounded-md transition-colors flex items-center disabled:opacity-50"
              >
                <Play className="w-4 h-4 mr-1.5" /> Run Code
              </button>
              <button
                onClick={() => handleRun(true)}
                disabled={status !== SUBMISSION_STATUS.IDLE && status !== SUBMISSION_STATUS.DONE && status !== SUBMISSION_STATUS.ERROR}
                className="px-4 py-1.5 bg-brand-600 hover:bg-brand-500 text-white text-sm font-medium rounded-md transition-colors flex items-center disabled:opacity-50"
              >
                <Send className="w-4 h-4 mr-1.5" /> Submit
              </button>
            </div>
          </div>

          {/* Editor Container */}
          <div className="flex-grow min-h-0 relative border-b border-[#30363d]">
            <CodeEditor 
              value={code} 
              onChange={setCode} 
              language={language.monacoId} 
              readOnly={status !== SUBMISSION_STATUS.IDLE && status !== SUBMISSION_STATUS.DONE && status !== SUBMISSION_STATUS.ERROR}
            />
          </div>

          {/* Output Panel (fixed height or resizable, sticking to fixed for now) */}
          <div className="h-[250px] flex-shrink-0">
              <TestCasePanel
                results={results}
                status={status}
                metrics={metrics}
                testCases={problem?.publicTestCases}
                executionSummary={executionSummary}
              />
          </div>
        </div>
      </main>
    </div>
  );
};

export default CodingPage;
