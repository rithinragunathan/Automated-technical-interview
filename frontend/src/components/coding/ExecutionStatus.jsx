import React from 'react';
import { Loader2, CheckCircle, AlertCircle, PlayCircle, Code2 } from 'lucide-react';
import { SUBMISSION_STATUS } from '../../utils/constants';

const ExecutionStatus = ({ status, message }) => {
  if (status === SUBMISSION_STATUS.IDLE) return null;

  const getStatusConfig = () => {
    switch (status) {
      case SUBMISSION_STATUS.SUBMITTING:
        return { icon: PlayCircle, color: 'text-brand-400', text: 'Submitting code...' };
      case SUBMISSION_STATUS.COMPILING:
        return { icon: Code2, color: 'text-yellow-400', text: 'Compiling...' };
      case SUBMISSION_STATUS.RUNNING:
        return { icon: Loader2, color: 'text-brand-400', text: 'Running test cases...', spin: true };
      case SUBMISSION_STATUS.EVALUATING:
        return { icon: Loader2, color: 'text-purple-400', text: 'Evaluating results...', spin: true };
      case SUBMISSION_STATUS.DONE:
        return { icon: CheckCircle, color: 'text-green-500', text: 'Execution complete' };
      case SUBMISSION_STATUS.ERROR:
        return { icon: AlertCircle, color: 'text-red-500', text: 'Execution failed' };
      default:
        return { icon: Loader2, color: 'text-gray-400', text: 'Processing...' };
    }
  };

  const config = getStatusConfig();
  const Icon = config.icon;

  return (
    <div className="flex items-center space-x-2 text-sm font-medium">
      <Icon className={`w-4 h-4 ${config.color} ${config.spin ? 'animate-spin' : ''}`} />
      <span className="text-gray-300">
        {config.text}
        {status !== SUBMISSION_STATUS.DONE && status !== SUBMISSION_STATUS.ERROR && (
          <span className="animate-pulse">...</span>
        )}
      </span>
      {message && <span className="text-gray-500 ml-2 border-l border-[#30363d] pl-2">{message}</span>}
    </div>
  );
};

export default ExecutionStatus;
