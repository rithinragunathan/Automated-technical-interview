import React from 'react';
import { Link } from 'react-router-dom';
import { Clock, Calendar, ChevronRight } from 'lucide-react';
import Badge from '../common/Badge';
import { formatDate } from '../../utils/formatDate';
import { getStatusColor, getScoreColor } from '../../utils/scoreHelpers';

const InterviewCard = ({ interview }) => {
  return (
    <div className="bg-[#161b22] border border-[#30363d] rounded-lg p-5 hover:border-[#484f58] transition-colors flex flex-col h-full">
      <div className="flex justify-between items-start mb-4">
        <div>
          <h3 className="text-lg font-semibold text-white mb-1">{interview.topic}</h3>
          <p className="text-sm text-gray-400">{interview.type.replace('_', ' ')}</p>
        </div>
        <Badge variant={getStatusColor(interview.status)} text={interview.status} />
      </div>

      <div className="flex-grow space-y-3 mb-6">
        <div className="flex items-center text-sm text-gray-300">
          <Calendar className="w-4 h-4 mr-2 text-gray-500" />
          {formatDate(interview.date)}
        </div>
        <div className="flex items-center text-sm text-gray-300">
          <Clock className="w-4 h-4 mr-2 text-gray-500" />
          {interview.duration} mins
        </div>
        {interview.score !== null && (
          <div className="flex items-center text-sm mt-4">
            <span className="text-gray-400 mr-2">Score:</span>
            <span 
              className="font-bold text-lg"
              style={{ color: getScoreColor(interview.score) }}
            >
              {interview.score}%
            </span>
          </div>
        )}
      </div>

      <Link 
        to={`/interviews/${interview.id}/${interview.status === 'COMPLETED' ? 'report' : 'live'}`}
        className="w-full flex items-center justify-center px-4 py-2 bg-[#30363d] hover:bg-[#484f58] text-white rounded-md transition-colors text-sm font-medium"
      >
        {interview.status === 'COMPLETED' ? 'View Report' : 'Resume Interview'}
        <ChevronRight className="w-4 h-4 ml-1" />
      </Link>
    </div>
  );
};

export default InterviewCard;
