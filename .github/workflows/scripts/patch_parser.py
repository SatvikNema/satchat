from typing import Tuple, List

test_input = """
@@ -23,6 +23,8 @@ public class ChatService {
 
   private final OnlineOfflineService onlineOfflineService;
 
+  private static final String SOMETHING = "something";
+
   @Autowired
   public ChatService(
       SimpMessageSendingOperations simpMessageSendingOperations,
@@ -75,6 +77,14 @@ public void sendMessageToConvId(
     simpMessageSendingOperations.convertAndSend("/topic/" + conversationId, chatMessage);
   }
 
+  public String functionInBetween() {
+    String result = "";
+    for (int i = 1; i <= 100_000; i++) {
+      result += String.valueOf(i);
+    }
+    return result;
+  }
+
   private void populateContext(ChatMessage chatMessage, UserDetailsImpl userDetails) {
     chatMessage.setSenderUsername(userDetails.getUsername());
     chatMessage.setSenderId(userDetails.getId());
"""

test_input2 = """
@@ -32,6 +32,8 @@ public class OnlineOfflineService {
   private final UserRepository userRepository;
   private final SimpMessageSendingOperations simpMessageSendingOperations;
 
+  private static final String IS_ACTIVE = "is_active";
+
   public OnlineOfflineService(
       UserRepository userRepository, SimpMessageSendingOperations simpMessageSendingOperations) {
     this.onlineUsers = new ConcurrentSkipListSet<>();
@@ -150,4 +152,8 @@ public void notifySender(
               .build());
     }
   }
+
+  public void display() {
+    System.out.println("this is a function");
+  }
 }
"""


# say the patch has 3 hunks:
# old file 1 to 10, new file 1 to 20
# old file 30 to 40 new file 23 to 44
# old file 50 to 60 new file 48 to 55
# output will be [((1, 10), (1, 20)), ((30, 40), (23, 44)), ((50, 40), (48, 55))]
def get_lines_changed(data) -> list[tuple[tuple[int, int], tuple[int, int]]]:
    lines_start_end_pairs = []
    data = data.split("\n")
    for line in data:
        line_numbers_spread = ''
        if line.startswith("@@") and " @@" in line:
            for i in range(3, len(line)):
                line_numbers_spread += line[i]
                if line_numbers_spread[-2:] == "@@":
                    break
            parsed_lines = parse_line_spread(line_numbers_spread)
            lines_start_end_pairs.append(parsed_lines)

    return lines_start_end_pairs


# gets an input like '-32,6 +32,8 @@'
# returns ((32, 38), (32, 40))
def parse_line_spread(line_numbers_spread: str) -> tuple[tuple[int, int], tuple[int, int]]:
    line_numbers_spread = line_numbers_spread[:-3].split(" ")
    old_file_lines = get_line_range(line_numbers_spread[0])
    new_file_lines = get_line_range(line_numbers_spread[1])
    return old_file_lines, new_file_lines


# gets an input like '-32,8'
# returns (32, 40)
def get_line_range(line_numbers_spread: str) -> Tuple[int, int]:
    lines = line_numbers_spread.split(",")
    line_start = abs(int(lines[0]))
    line_end = line_start + int(lines[1])
    return line_start, line_end


def get_file_portion(file_content, line_limits: Tuple[int, int]) -> str:
    start, end = line_limits
    trimmed_content: str = ""
    if end - start > 1:  # ignore one line changes
        file_contents_split_by_line = file_content.decoded_content.decode('utf-8').split("\n")
        trimmed_content = "\n".join(file_contents_split_by_line[start: end + 1])
    return trimmed_content

# print(get_lines_changed(test_input))
# print(get_lines_changed(test_input2))
