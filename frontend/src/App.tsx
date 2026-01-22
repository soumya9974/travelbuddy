import { useState, useEffect } from "react";
import { AuthProvider, useAuth } from "./context/AuthContext";
import { Login } from "./pages/Login";
import { Register } from "./pages/Register";
import { ForgotPassword } from "./pages/ForgotPassword";
import { Home } from "./pages/Home";
import { Groups } from "./pages/Groups";
import { GroupDetail } from "./pages/GroupDetail";
import { Profile } from "./pages/Profile";
import { Navbar } from "./components/layout/Navbar";
import { TravelGroup } from "./types";
import { Spinner } from "./components/ui/Spinner";

function AppContent() {
  const { isAuthenticated, loading } = useAuth();

  const [authView, setAuthView] = useState<
    "login" | "register" | "forgot-password"
  >("login");

  const [currentPage, setCurrentPage] = useState<
    "home" | "groups" | "profile" | "group-detail"
  >("home");

  const [selectedGroup, setSelectedGroup] = useState<TravelGroup | null>(null);

  // ✅ Reset app state on login OR logout
  useEffect(() => {
    if (!isAuthenticated) {
      setAuthView("login");
      setCurrentPage("home");
      setSelectedGroup(null);
      return;
    }

    // On successful login
    setCurrentPage("home");
    setSelectedGroup(null);
  }, [isAuthenticated]);

  // ✅ Prevent broken state on refresh
  useEffect(() => {
    if (currentPage === "group-detail" && !selectedGroup) {
      setCurrentPage("groups");
    }
  }, [currentPage, selectedGroup]);

  if (loading) {
    return (
      <div className="min-h-screen relative flex items-center justify-center">
        <div
          className="absolute inset-0 bg-cover bg-center bg-no-repeat"
          style={{
            backgroundImage:
              "url('https://images.pexels.com/photos/1007657/pexels-photo-1007657.jpeg?auto=compress&cs=tinysrgb&w=1920')",
          }}
        >
          <div className="absolute inset-0 bg-gradient-to-br from-blue-50/95 via-cyan-50/95 to-teal-50/95" />
        </div>
        <div className="relative z-10">
          <Spinner size="lg" />
        </div>
      </div>
    );
  }

  // ---------------- AUTH VIEWS ----------------
  if (!isAuthenticated) {
    if (authView === "login") {
      return (
        <Login
          onSwitchToRegister={() => setAuthView("register")}
          onSwitchToForgotPassword={() => setAuthView("forgot-password")}
        />
      );
    }

    if (authView === "register") {
      return <Register onSwitchToLogin={() => setAuthView("login")} />;
    }

    if (authView === "forgot-password") {
      return <ForgotPassword onBackToLogin={() => setAuthView("login")} />;
    }
  }

  // ---------------- NAVIGATION HANDLERS ----------------
  const handleSelectGroup = (group: TravelGroup) => {
    setSelectedGroup(group);
    setCurrentPage("group-detail");
  };

  const handleBackToGroups = () => {
    setSelectedGroup(null);
    setCurrentPage("groups");
  };

  const handleNavigate = (page: "home" | "groups" | "profile") => {
    setCurrentPage(page);
    setSelectedGroup(null);
  };

  // ---------------- MAIN APP ----------------
  return (
    <div className="min-h-screen relative">
      <Navbar onNavigate={handleNavigate} currentPage={currentPage} />

      <main
        className={
          currentPage === "home"
            ? ""
            : "max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8"
        }
      >
        {currentPage === "home" && (
          <Home onGetStarted={() => setCurrentPage("groups")} />
        )}

        {currentPage === "groups" && (
          <Groups onSelectGroup={handleSelectGroup} />
        )}

        {currentPage === "profile" && <Profile />}

        {currentPage === "group-detail" && selectedGroup && (
          <GroupDetail group={selectedGroup} onBack={handleBackToGroups} />
        )}
      </main>
    </div>
  );
}

function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}

export default App;
