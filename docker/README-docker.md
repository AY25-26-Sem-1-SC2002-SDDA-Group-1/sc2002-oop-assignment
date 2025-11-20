# Internship Placement System - Docker Quick Start

## How to Build and Run (No Setup Needed)

1. **Open a terminal in the `docker` folder:**

   ```sh
   cd path/to/sc2002-oop-assignment/docker
   ```

2. **Build the Docker image:**

   ```sh
   docker build -t internship-system .
   ```

3. **Run the CLI app interactively:**

   ```sh
   docker run -it internship-system
   ```

   - The app will use the built-in `data` folder and CSV files inside the image.
   - No volume mount is needed for basic use.

4. **(Optional) Persist data changes:**
   If you want your data to survive after the container is deleted, run:
   ```sh
   docker run -it -v ${PWD}/../data:/app/data internship-system
   ```
   - On Windows PowerShell, use:
     ```sh
     docker run -it -v ${PWD}\..\data:/app/data internship-system
     ```
   - Or, with absolute path:
     ```sh
     docker run -it -v C:\Users\yourname\sc2002-oop-assignment\data:/app/data internship-system
     ```

## What happens?

- The app runs out-of-the-box with sample data.
- All code and CSV files are inside the image.
- No setup or configuration required for basic use.
- Advanced users can mount their own data folder if needed.

## Troubleshooting

- If you see file not found errors, make sure the `data` folder exists in your project before building.
- If you want to reset the data, rebuild the image.

---

## How to Share and Use the Image

### 1. Push your image to Docker Hub

1. Log in to Docker Hub:
   ```sh
   docker login
   ```
2. Tag your image (if needed):
   ```sh
   docker tag internship-system yourdockerhub/internship-system
   ```
3. Push the image:
   ```sh
   docker push yourdockerhub/internship-system
   ```

### 2. For your friend (or anyone else)

1. Pull the image:
   ```sh
   docker pull yourdockerhub/internship-system
   ```
2. Run the app interactively:

   ```sh
   docker run -it yourdockerhub/internship-system
   ```

   - The app will work out-of-the-box with the built-in data folder and CSV files.
   - No setup, no volume mount, no extra files needed.

3. (Optional) To persist data changes:
   ```sh
   docker run -it -v ${PWD}/data:/app/data yourdockerhub/internship-system
   ```
   - This will use their own data folder and keep changes after the container is deleted.

---

**Summary:**

- Build and push the image once.
- Anyone can pull and run it immediately—no setup required!
- Advanced users can mount their own data folder if they want persistent changes.
