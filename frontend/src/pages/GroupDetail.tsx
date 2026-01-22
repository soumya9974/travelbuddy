import { useState, useEffect } from "react";
import { useAuth } from "../context/AuthContext";
import { TravelGroup, Membership } from "../types";
import { apiRequest } from "../utils/api";
import { Card, CardBody, CardHeader } from "../components/ui/Card";
import { Button } from "../components/ui/Button";
import { Spinner } from "../components/ui/Spinner";
import {
  ArrowLeft,
  MapPin,
  Calendar,
  Users,
  MessageSquare,
  List,
  Trash2,
} from "lucide-react";
import { GroupChat } from "../components/groups/GroupChat";
import { ItineraryList } from "../components/itinerary/ItineraryList";

interface GroupDetailProps {
  group: TravelGroup;
  onBack: () => void;
}

export const GroupDetail = ({ group, onBack }: GroupDetailProps) => {
  const { token, user } = useAuth();
  const [members, setMembers] = useState<Membership[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<"chat" | "itinerary" | "members">(
    "chat"
  );
  const [isAdmin, setIsAdmin] = useState(false); // ✅ NEW
  const [isMember, setIsMember] = useState(false);

  useEffect(() => {
    fetchMembers();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [group.id]);

  const fetchMembers = async () => {
    setLoading(true);
    try {
      const data: Membership[] = await apiRequest(
        `/groups/${group.id}/memberships`,
        { token: token! }
      );
      setMembers(data);

      const myMembership = data.find(
        (m) => Number(m.userId) === Number(user?.id)
      );
      setIsMember(!!myMembership);
      setIsAdmin(myMembership?.role === "ADMIN"); // ✅ ADMIN CHECK
    } catch (err) {
      console.error("Failed to fetch members:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleJoinGroup = async () => {
    try {
      await apiRequest(`/groups/${group.id}/memberships`, {
        method: "POST",
        token: token!,
      });
      fetchMembers();
      setActiveTab("chat");
    } catch (err) {
      console.error("Failed to join group:", err);
    }
  };

  const handleLeaveGroup = async () => {
    try {
      await apiRequest(`/groups/${group.id}/memberships`, {
        method: "DELETE",
        token: token!,
      });
      setIsMember(false);
      setIsAdmin(false); // ✅ RESET ADMIN STATUS
      onBack();
    } catch (err) {
      console.error("Failed to leave group:", err);
    }
  };
  // ✅ DELETE GROUP (ADMIN ONLY)
  const handleDeleteGroup = async () => {
    if (!window.confirm("Are you sure you want to delete this group?")) return;
    try {
      await apiRequest(`/groups/${group.id}`, {
        method: "DELETE",
        token: token!,
      });
      onBack(); // go back to groups list
    } catch (err) {
      console.error("Failed to delete group:", err);
      alert("Only admins can delete this group.");
    }
  };

  const formatDate = (date: string) =>
    new Date(date).toLocaleDateString("en-US", {
      month: "long",
      day: "numeric",
      year: "numeric",
    });

  return (
    <div className="relative min-h-screen pb-12">
      {/* ✅ Background */}
      <div
        className="fixed inset-0 -z-10 bg-cover bg-center bg-no-repeat"
        style={{
          backgroundImage:
            "url('https://images.pexels.com/photos/210186/pexels-photo-210186.jpeg')",
        }}
      >
        <div className="absolute inset-0 bg-gradient-to-br from-blue-50/60 via-cyan-50/60 to-teal-50/60" />
      </div>

      {/* ✅ Foreground content */}
      <div className="relative z-10 px-4">
        {/* Back button */}
        <Button
          variant="ghost"
          onClick={onBack}
          className="mb-6 hover:bg-white shadow-sm"
        >
          <ArrowLeft size={20} className="mr-2" />
          Back to Groups
        </Button>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Left column */}
          <div className="lg:col-span-1 space-y-4">
            <Card>
              <CardBody>
                <h1 className="text-2xl font-bold mb-2">{group.name}</h1>

                <div className="space-y-2 text-sm text-gray-600">
                  <div className="flex items-center">
                    <MapPin size={16} className="mr-2 text-blue-600" />
                    {group.destination}
                  </div>
                  <div className="flex items-center">
                    <Calendar size={16} className="mr-2 text-blue-600" />
                    {formatDate(group.startDate)} - {formatDate(group.endDate)}
                  </div>
                  <div className="flex items-center">
                    <Users size={16} className="mr-2 text-blue-600" />
                    {members.length} members
                  </div>
                </div>

                <p className="mt-4 text-gray-700 text-sm">
                  {group.description}
                </p>

                {!isMember && (
                  <Button className="mt-4 w-full" onClick={handleJoinGroup}>
                    Join Group
                  </Button>
                )}

                {isMember && (
                  <Button
                    className="mt-4 w-full"
                    variant="outline"
                    onClick={handleLeaveGroup}
                  >
                    Leave Group
                  </Button>
                )}
                {/* ✅ ADMIN DELETE BUTTON */}
                {isAdmin && (
                  <Button
                    className="mt-3 w-full bg-red-600 hover:bg-red-700 text-white"
                    onClick={handleDeleteGroup}
                  >
                    <Trash2 size={16} className="mr-2" />
                    Delete Group
                  </Button>
                )}
              </CardBody>
            </Card>
          </div>

          {/* Right column */}
          <div className="lg:col-span-2">
            <Card>
              <CardHeader>
                <div className="flex space-x-2 border-b">
                  <button
                    onClick={() => setActiveTab("chat")}
                    className={`px-4 py-2 font-medium ${
                      activeTab === "chat"
                        ? "text-blue-600 border-b-2 border-blue-600"
                        : "text-gray-600 hover:text-gray-900"
                    }`}
                  >
                    <MessageSquare size={16} className="inline mr-1" />
                    Chat
                  </button>
                  <button
                    onClick={() => setActiveTab("itinerary")}
                    className={`px-4 py-2 font-medium ${
                      activeTab === "itinerary"
                        ? "text-blue-600 border-b-2 border-blue-600"
                        : "text-gray-600 hover:text-gray-900"
                    }`}
                  >
                    <List size={16} className="inline mr-1" />
                    Itinerary
                  </button>
                  <button
                    onClick={() => setActiveTab("members")}
                    className={`px-4 py-2 font-medium ${
                      activeTab === "members"
                        ? "text-blue-600 border-b-2 border-blue-600"
                        : "text-gray-600 hover:text-gray-900"
                    }`}
                  >
                    <Users size={16} className="inline mr-1" />
                    Members
                  </button>
                </div>
              </CardHeader>

              <CardBody className="p-0">
                {activeTab === "chat" && (
                  <div className="p-4">
                    <GroupChat
                      groupId={group.id}
                      isMember={isMember}
                      isAdmin={isAdmin}
                    />
                  </div>
                )}

                {activeTab === "itinerary" && (
                  <div className="p-4">
                    <ItineraryList groupId={group.id} />
                  </div>
                )}

                {activeTab === "members" && (
                  <div className="p-4 space-y-2">
                    {loading ? (
                      <Spinner />
                    ) : members.length === 0 ? (
                      <p className="text-gray-500">No members yet.</p>
                    ) : (
                      members.map((m) => (
                        <div
                          key={m.id}
                          className="flex justify-between items-center p-2 bg-gray-50 rounded"
                        >
                          <div>
                            <p className="font-medium text-gray-900">
                              {m.userName}
                            </p>
                            <p className="text-xs text-gray-500">
                              Role: {m.role}
                            </p>
                          </div>
                        </div>
                      ))
                    )}
                  </div>
                )}
              </CardBody>
            </Card>
          </div>
        </div>
      </div>
    </div>
  );
};
