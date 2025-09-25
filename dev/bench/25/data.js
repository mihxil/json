window.BENCHMARK_DATA = {
  "lastUpdate": 1758834226872,
  "repoUrl": "https://github.com/mihxil/json",
  "entries": {
    "Benchmark": [
      {
        "commit": {
          "author": {
            "email": "michiel.meeuwissen@gmail.com",
            "name": "Michiel Meeuwissen",
            "username": "mihxil"
          },
          "committer": {
            "email": "michiel.meeuwissen@gmail.com",
            "name": "Michiel Meeuwissen",
            "username": "mihxil"
          },
          "distinct": true,
          "id": "e9610179650dff0241c115b0ea23317567d70cbe",
          "message": "Link to bench marks.",
          "timestamp": "2025-09-25T23:00:49+02:00",
          "tree_id": "231bc6c0345d354318a310dfcb5ced7214deca82",
          "url": "https://github.com/mihxil/json/commit/e9610179650dff0241c115b0ea23317567d70cbe"
        },
        "date": 1758834226429,
        "tool": "jmh",
        "benches": [
          {
            "name": "org.meeuw.MyBenchmark.baseline",
            "value": 2301080441.9634767,
            "unit": "ops/s",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "org.meeuw.MyBenchmark.grep",
            "value": 401516.5008826856,
            "unit": "ops/s",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "org.meeuw.MyBenchmark.grepMain",
            "value": 193033.53002658347,
            "unit": "ops/s",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          }
        ]
      }
    ]
  }
}