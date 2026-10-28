import React from 'react';
import { NavLink } from 'react-router-dom';
import { LayoutDashboard, Code, History, BarChart2, Lightbulb, Settings, FileCode } from 'lucide-react';
import useAuth from '../../hooks/useAuth';

const Sidebar = ({ isOpen, onClose }) => {
  const { isAdmin } = useAuth();

  const links = [
    { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
    { to: '/problems', icon: Code, label: 'Problems' },
    { to: '/interviews', icon: History, label: 'Interviews' },
    { to: '/analytics', icon: BarChart2, label: 'Analytics' },
    { to: '/recommendations', icon: Lightbulb, label: 'Recommendations' },
  ];

  const adminLinks = [
    { to: '/admin', icon: Settings, label: 'Admin Dashboard' },
    { to: '/admin/problems', icon: FileCode, label: 'Manage Problems' },
  ];

  return (
    <>
      {/* Mobile Backdrop */}
      {isOpen && (
        <div 
          className="fixed inset-0 bg-black/50 z-40 lg:hidden"
          onClick={onClose}
        ></div>
      )}

      {/* Sidebar */}
      <aside className={`app-sidebar fixed top-[4.5rem] left-0 h-[calc(100vh-4.5rem)] w-64 bg-[#0f1117] border-r border-[#30363d] z-40 transform transition-transform duration-300 ease-in-out lg:translate-x-0 overflow-y-auto ${
        isOpen ? 'translate-x-0' : '-translate-x-full'
      }`}>
        <div className="py-6 px-4">
          <nav className="space-y-1">
            <div className="text-[11px] font-semibold text-gray-500 uppercase tracking-[0.12em] mb-3 px-3">
              Main Menu
            </div>
            {links.map((link) => (
              <NavLink
                key={link.to}
                to={link.to}
                onClick={() => onClose()}
                className={({ isActive }) =>
                  `flex items-center px-3 py-2 text-sm font-medium rounded-md transition-colors ${
                    isActive
                      ? 'bg-brand-600/10 text-brand-500 border border-brand-500/20'
                      : 'text-gray-300 hover:bg-[#161b22] hover:text-white'
                  }`
                }
              >
                <link.icon className="w-5 h-5 mr-3 flex-shrink-0" />
                {link.label}
              </NavLink>
            ))}
          </nav>

          {isAdmin && (
            <nav className="mt-8 space-y-1">
              <div className="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2 px-3">
                Admin
              </div>
              {adminLinks.map((link) => (
                <NavLink
                  key={link.to}
                  to={link.to}
                  onClick={() => onClose()}
                  className={({ isActive }) =>
                    `flex items-center px-3 py-2 text-sm font-medium rounded-md transition-colors ${
                      isActive
                        ? 'bg-purple-600/10 text-purple-400 border border-purple-500/20'
                        : 'text-gray-300 hover:bg-[#161b22] hover:text-white'
                    }`
                  }
                >
                  <link.icon className="w-5 h-5 mr-3 flex-shrink-0" />
                  {link.label}
                </NavLink>
              ))}
            </nav>
          )}
        </div>
      </aside>
    </>
  );
};

export default Sidebar;
