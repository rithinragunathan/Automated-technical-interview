import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Menu, X, Brain, LogOut, User } from 'lucide-react';
import useAuth from '../../hooks/useAuth';

const Navbar = ({ onMenuClick }) => {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();
  const [showProfileMenu, setShowProfileMenu] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="bg-[#161b22] border-b border-[#30363d] h-16 fixed w-full top-0 z-40 flex items-center justify-between px-4">
      <div className="flex items-center">
        <button 
          onClick={onMenuClick}
          className="mr-4 p-1 rounded-md text-gray-400 hover:text-white hover:bg-[#30363d] lg:hidden"
        >
          <Menu className="w-6 h-6" />
        </button>
        <Link to="/dashboard" className="flex items-center text-brand-500 hover:text-brand-400 transition-colors">
          <Brain className="w-8 h-8 mr-2" />
          <span className="text-xl font-bold text-white tracking-tight">Mock<span className="text-brand-500">IQ</span></span>
        </Link>
      </div>

      <div className="flex items-center">
        {isAdmin && (
          <Link to="/admin" className="mr-4 text-sm font-medium text-gray-400 hover:text-white hidden md:block">
            Admin Panel
          </Link>
        )}
        <div className="relative">
          <button 
            onClick={() => setShowProfileMenu(!showProfileMenu)}
            className="flex items-center space-x-2 p-1 rounded-full hover:bg-[#30363d] transition-colors focus:outline-none"
          >
            <div className="w-8 h-8 rounded-full bg-brand-600 flex items-center justify-center text-white font-medium">
              {user?.name?.charAt(0)?.toUpperCase() || 'U'}
            </div>
          </button>

          {showProfileMenu && (
            <>
              <div 
                className="fixed inset-0 z-40"
                onClick={() => setShowProfileMenu(false)}
              ></div>
              <div className="absolute right-0 mt-2 w-48 bg-[#161b22] border border-[#30363d] rounded-md shadow-lg z-50 py-1">
                <div className="px-4 py-2 border-b border-[#30363d]">
                  <p className="text-sm font-medium text-white truncate">{user?.name}</p>
                  <p className="text-xs text-gray-400 truncate">{user?.email}</p>
                </div>
                <Link 
                  to="/profile" 
                  className="flex items-center px-4 py-2 text-sm text-gray-300 hover:bg-[#30363d] hover:text-white"
                  onClick={() => setShowProfileMenu(false)}
                >
                  <User className="w-4 h-4 mr-2" />
                  Profile
                </Link>
                <button 
                  onClick={() => {
                    setShowProfileMenu(false);
                    handleLogout();
                  }}
                  className="w-full flex items-center px-4 py-2 text-sm text-red-400 hover:bg-red-900/20"
                >
                  <LogOut className="w-4 h-4 mr-2" />
                  Sign out
                </button>
              </div>
            </>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
