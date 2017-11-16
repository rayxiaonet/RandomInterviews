/*jslint node: true */

'use strict';

module.exports = function (grunt) {
  var tag = grunt.option('tag') || 'SNAPSHOT';
  grunt.initConfig({
      pkg: grunt.file.readJSON('package.json'),

      jshint: {
        files: ['Gruntfile.js', 'common/models/*.js', 'server/**/*.js'],
        options: {
          jshintrc: true
        }
      },
      run: {

        local: {
          exec: 'PORT=3003 node server/server.js'
        }
      }
    }
  );

  grunt.loadNpmTasks('grunt-contrib-jshint');
  grunt.loadNpmTasks('grunt-run');

  grunt.registerTask('default', ['jshint']);
  grunt.registerTask('local', ['jshint', 'run:local']);
}
;
