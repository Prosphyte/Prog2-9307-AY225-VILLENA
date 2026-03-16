Students must:
Use Scanner for file path input
Use File class for validation
Loop until valid path

Example structure (simplified):

Scanner input = new Scanner(System.in);
File file;

while (true) {
    System.out.print("Enter dataset file path: ");
    String path = input.nextLine();
    file = new File(path);

    if (file.exists() && file.isFile()) {
        break;
    } else {
        System.out.println("Invalid file path. Please try again.");
    }
}