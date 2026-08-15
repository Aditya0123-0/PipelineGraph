import { NavLink, Routes, Route, Navigate } from "react-router-dom";
import ExplorerPage from "./pages/ExplorerPage.jsx";
import AnalyticsPage from "./pages/AnalyticsPage.jsx";

export default function App() {
  return (
    <div className="app-shell">
      <header className="app-header">
        <div className="app-brand">
          <span className="app-brand-mark">PG</span>
          <span className="app-brand-name">PipelineGraph</span>
        </div>
        <nav className="app-nav">
          <NavLink to="/explorer" className={({ isActive }) => `app-nav-link${isActive ? " is-active" : ""}`}>
            Explorer
          </NavLink>
          <NavLink to="/analytics" className={({ isActive }) => `app-nav-link${isActive ? " is-active" : ""}`}>
            Analytics
          </NavLink>
        </nav>
      </header>

      <main className="app-main">
        <Routes>
          <Route path="/" element={<Navigate to="/explorer" replace />} />
          <Route path="/explorer" element={<ExplorerPage />} />
          <Route path="/analytics" element={<AnalyticsPage />} />
        </Routes>
      </main>
    </div>
  );
}