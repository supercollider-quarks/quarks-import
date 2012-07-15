#/***************************************************************************
# *   This is the build script for libdatanetwork                           *
# *                                                                         *
# *   Copyright (C) 2009 by Marije Baalman                                  *
# *   nescivi _at_ gmail _dot_ com                                          *
# *                                                                         *
# *   This library is free software; you can redistribute it and/or modify  *
# *   it under the terms of the GNU Lesser General Public License as        *
# *   published by the Free Software Foundation; either version 3 of the    *
# *   License, or (at your option) any later version.                       *
# *                                                                         *
# *   This program is distributed in the hope that it will be useful,       *
# *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
# *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
# *   GNU General Public License for more details.                          *
# *                                                                         *
# *   You should have received a copy of the GNU General Public License     *
# *   along with this program; if not, write to the                         *
# *   Free Software Foundation, Inc.,                                       *
# *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
# ***************************************************************************/

EnsureSConsVersion(0,96)
EnsurePythonVersion(2,3)
SConsignFile()

import os
import re
import tarfile

PACKAGE = 'libdatanetwork'
VERSION = '0.4'

################ FUNCTIONS ##################

def createEnvironment(*keys):
    env = os.environ
    res = {}
    for key in keys:
        if env.has_key(key):
            res[key] = env[key]
    return res

def CheckPKGConfig( context, version ):
    context.Message( 'pkg-config installed? ' )
    ret = context.TryAction('pkg-config --atleast-pkgconfig-version=%s' % version)[0]
    context.Result( ret )
    return ret

def LookForPackage( context, name ):
    context.Message('%s installed? ' % name)
    ret = context.TryAction('pkg-config --exists \'%s\'' % name)[0]
    context.Result( ret )
    return ret

print "\n=============== " + PACKAGE + ' v' + VERSION + " ================="
print "=========== (c) 2009 - Marije Baalman ============== "
print "=========== http://sensestage.hexagram.ca ==========\n"
# Read options from the commandline
opts = Variables(None, ARGUMENTS)
opts.AddVariables(
    PathVariable('PREFIX', 'Set the installation directory', '/usr/local'),
)

env = Environment(options = opts,
                  ENV     = createEnvironment('PATH', 'PKG_CONFIG_PATH'),
                  PACKAGE = PACKAGE,
                  VERSION = VERSION,
                  URL     = 'http://sensestage.hexagram.ca',
                  TARBALL = PACKAGE + '.' + VERSION + '.tbz2'
		)

# Help
Help("""
Command Line options:
scons -h         (libdn help) 
scons -H         (SCons help) 
      """)

Help( opts.GenerateHelpText( env ) )


# ensure that pkg-config and external dependencies are ok
# and parse them
conf = env.Configure(custom_tests = {  'CheckPKGConfig' : CheckPKGConfig,
                                       'LookForPackage' : LookForPackage })

if not conf.CheckPKGConfig('0'):
    print 'pkg-config not found.'
    Exit(1)

# liblo is needed
if not conf.LookForPackage('liblo'):
    if conf.CheckLib ('lo', 'lo_server_new') == False:
        print "liblo does not appear to be installed."
        Exit(1)
else:
    env.ParseConfig('pkg-config --cflags --libs liblo')



# curl is needed
if not conf.LookForPackage('libcurl'):
    Exit(1)
env.ParseConfig('pkg-config --cflags --libs libcurl')

env = conf.Finish()

LIBFILES = Split('''
src/datanetwork.cpp
src/datanetworkosc.cpp
src/datanode.cpp
src/dataslot.cpp
src/minibee.cpp
src/oscin.cpp
''')

HEADERFILES = Split('''
include/datanetwork.h
include/datanetworkosc.h
include/datanode.h
include/dataslot.h
include/minibee.h
include/oscin.h
''')


env.Append(
  CPPPATH = ['include/'],
  CXXFLAGS = '-O2 -g3'
)

lib = env.SharedLibrary('lib/libdatanetwork', LIBFILES )

env2 = Environment(options = opts,
                  ENV     = createEnvironment('PATH', 'PKG_CONFIG_PATH'),
                  PACKAGE = PACKAGE,
                  VERSION = VERSION,
                  URL     = 'http://sensestage.hexagram.ca',
                  TARBALL = PACKAGE + '.' + VERSION + '.tbz2'
		)
		
prog = env2.Program('bin/datanetwork_example', 'examples/libdn.cpp')

env2.Append(
  CPPPATH = ['include/'],
  LIBPATH = ['lib/'],
  LIBS = ['datanetwork'],
  CXXFLAGS = '-O2 -g3'
)

LIB_DIR = env['PREFIX'] + "/lib"
INCLUDE_DIR = env['PREFIX'] + "/include/libdatanetwork"
BIN_DIR = env2['PREFIX'] + "/bin"

# env.Alias( 'install', env.Install(BIN_DIR, prog) )
env.Alias( 'install', env.Install(LIB_DIR, lib) )
env.Alias( 'install', env.Install(INCLUDE_DIR, HEADERFILES) )

DIST_FILES = Split('''
README
COPYING
COPYING.LESSER
Doxyfile
SConstruct
Makefile
''')

ANY_FILE_RE = re.compile('.*')
SRC_FILE_RE = re.compile('.*\.(c(pp)|h)$')

DIST_SPECS = [
    ('src', SRC_FILE_RE ),
    ('include', SRC_FILE_RE ),
    ('examples', SRC_FILE_RE )
    ]

def dist_paths():
    paths = DIST_FILES[:]
    specs = DIST_SPECS[:]
    while specs:
        base, re = specs.pop()
        if not re: re = ANY_FILE_RE
        for root, dirs, files in os.walk(base):
            if 'CVS' in dirs: dirs.remove('CVS')
            if '.svn' in dirs: dirs.remove('.svn')
            for path in dirs[:]:
                if re.match(path):
                    specs.append((os.path.join(root, path), re))
                    dirs.remove(path)
            for path in files:
                if re.match(path):
                    paths.append(os.path.join(root, path))
    paths.sort()
    return paths

def build_tar(env, target, source):
    paths = dist_paths()
    print paths
    tarfile_name = str(target[0])
    tar_name = os.path.splitext(os.path.basename(tarfile_name))[0]
    tar = tarfile.open(tarfile_name, "w:bz2")
    for path in paths:
        tar.add(path, os.path.join(tar_name, path))
    tar.close()

if 'dist' in COMMAND_LINE_TARGETS:
    env.Alias('dist', env['TARBALL'])
    env.Command(env['TARBALL'], 'SConstruct', build_tar)
